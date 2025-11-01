package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.HealthDataDailyResponse;
import com.kb.healthcare.myohui.domain.dto.HealthDataMonthlyResponse;
import com.kb.healthcare.myohui.domain.dto.HealthDataRequest;
import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.domain.enums.HealthProduct;
import com.kb.healthcare.myohui.domain.enums.HealthSource;
import com.kb.healthcare.myohui.domain.enums.PeriodType;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import com.kb.healthcare.myohui.global.exception.CustomException;
import com.kb.healthcare.myohui.repository.HealthDataDailyRepository;
import com.kb.healthcare.myohui.repository.HealthDataRawRepository;
import com.kb.healthcare.myohui.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MemberRepository memberRepository;
    private final HealthDataRawRepository healthDataRawRepository;
    private final HealthDataDailyRepository healthDataDailyRepository;

    /**
     * 건강 데이터 저장 및 일별 집계
     * */
    @Transactional
    public void saveHealthData(Long memberId, HealthDataRequest request) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String recordKey = request.getRecordKey();

        // 기존 raw 데이터 조회
        List<HealthDataRaw> existingList = healthDataRawRepository.findAllByRecordKeyAndMember(recordKey, member);

        // 기존 데이터 기간 set (중복 체크)
        Set<String> existingPeriods = existingList.stream()
            .map(r -> r.getPeriodFrom() + "|" + r.getPeriodTo())
            .collect(Collectors.toSet());

        List<HealthDataRaw> raws = new ArrayList<>();
        HealthSource source = HealthSource.from(request.getData().getSource().getName());
        HealthProduct product = HealthProduct.from(request.getData().getSource().getProduct().getName());

        for (HealthDataRequest.Entry entry : request.getData().getEntries()) {
            LocalDateTime from = LocalDateTime.parse(entry.getPeriod().getFrom(), FORMATTER);
            LocalDateTime to = LocalDateTime.parse(entry.getPeriod().getTo(), FORMATTER);
            String key = from + "|" + to;

            // 중복되지 않은 데이터만 추가
            if (!existingPeriods.contains(key)) {
                raws.add(new HealthDataRaw(
                    member, recordKey,
                    entry.getSteps(),
                    entry.getDistance().getValue(), entry.getDistance().getUnit(),
                    entry.getCalories().getValue(), entry.getCalories().getUnit(),
                    from, to, source, product
                ));
            }
        }

        if (raws.isEmpty()) {
            log.debug("신규 데이터 없음 - recordKey={}, memberId={}", recordKey, memberId);
            return;
        }

        // 신규 데이터만 저장 후 일별 집계
        healthDataRawRepository.saveAll(raws);
        aggregateDaily(member, recordKey, raws); // TODO 동기 -> 비동기
    }

    /**
     * 일별 건강 데이터 집계
     */
    private void aggregateDaily(Member member, String recordKey, List<HealthDataRaw> raws) {
        // raw 데이터 일자별 그룹핑
        Map<LocalDate, List<HealthDataRaw>> groupedByDate =
            raws.stream().collect(Collectors.groupingBy(r -> r.getPeriodFrom().toLocalDate()));

        if (groupedByDate.isEmpty()) return;

        List<LocalDate> recordDates = new ArrayList<>(groupedByDate.keySet());
        List<HealthDataDaily> existingList =
            healthDataDailyRepository.findAllByMemberAndRecordKeyAndRecordDateIn(member, recordKey, recordDates);

        Map<LocalDate, HealthDataDaily> existingMap = existingList.stream()
            .collect(Collectors.toMap(HealthDataDaily::getRecordDate, d -> d));

        List<HealthDataDaily> dailiesToSave = new ArrayList<>();

        // 일자별 합계 upsert
        for (Map.Entry<LocalDate, List<HealthDataRaw>> entry : groupedByDate.entrySet()) {
            LocalDate recordDate = entry.getKey();
            List<HealthDataRaw> dailyList = entry.getValue();

            int totalSteps = dailyList.stream().mapToInt(HealthDataRaw::getSteps).sum();
            float totalCalories = (float) dailyList.stream().mapToDouble(HealthDataRaw::getCalories).sum();
            float totalDistance = (float) dailyList.stream().mapToDouble(HealthDataRaw::getDistance).sum();

            HealthDataDaily existing = existingMap.get(recordDate);
            if (existing != null) {
                existing.update(totalSteps, totalCalories, totalDistance);
            } else {
                dailiesToSave.add(new HealthDataDaily(member, recordKey, recordDate, totalSteps, totalCalories, totalDistance));
            }
        }

        if (!dailiesToSave.isEmpty()) {
            healthDataDailyRepository.saveAll(dailiesToSave);
        }
    }

    /**
     * 건강 데이터 조회 (일별 / 월별)
     * */
    @Transactional(readOnly = true)
    public List<?> getHealthData(Long memberId, PeriodType period, LocalDate startDate, LocalDate endDate) {
        return switch (period) {
            case DAILY -> getDaily(memberId, startDate, endDate);
            case MONTHLY -> getMonthly(memberId, startDate, endDate);
        };
    }

    /**
     * 일별 데이터 조회
     * */
    private List<HealthDataDailyResponse> getDaily(Long memberId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(1900, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return healthDataDailyRepository.findAllByMemberIdAndRecordDateBetweenOrderByRecordDateDesc(memberId, start, end)
            .stream()
            .map(HealthDataDailyResponse::from)
            .toList();
    }

    /**
     * 월별 데이터 조회
     * */
    private List<HealthDataMonthlyResponse> getMonthly(Long memberId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(1900, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return healthDataDailyRepository.findMonthlyAggregates(memberId, start, end);
    }
}