package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.dto.HealthDataDailyResponse;
import com.kb.healthcare.myohui.domain.dto.HealthDataMonthlyResponse;
import com.kb.healthcare.myohui.domain.dto.HealthDataRequest;
import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.domain.enums.HealthProduct;
import com.kb.healthcare.myohui.domain.enums.HealthSource;
import com.kb.healthcare.myohui.domain.enums.PeriodType;
import com.kb.healthcare.myohui.global.cache.CacheService;
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
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DAILY_KEY = "health:daily:";
    private static final String MONTHLY_KEY = "health:monthly:";

    private final CacheService cacheService;
    private final MemberRepository memberRepository;
    private final HealthDataRawRepository healthDataRawRepository;
    private final HealthDataDailyRepository healthDataDailyRepository;
    private final HealthDataAggregator healthDataAggregator;

    /**
     * 건강 데이터 저장 및 일별 집계
     * */
    @Transactional
    public void saveHealthData(Long memberId, HealthDataRequest request) {
        // 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 raw 데이터 조회
        String recordKey = request.getRecordKey();
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

        log.info("aggregateDailyAsync call START (Thread: {})", Thread.currentThread().getName());
        healthDataAggregator.aggregateDailyAsync(member, recordKey, raws);

        // 캐시 무효화 (recordKey 기준)
        invalidateCache(memberId, recordKey);
    }

    /**
     * 캐시 무효화 (recordKey 단위)
     */
    private void invalidateCache(Long memberId, String recordKey) {
        cacheService.deleteCache(DAILY_KEY + memberId + ":" + recordKey);
        cacheService.deleteCache(MONTHLY_KEY + memberId + ":" + recordKey);
        log.info("[CACHE INVALIDATE] memberId={}, recordKey={}", memberId, recordKey);
    }

    /**
     * recordKey가 회원 소유인지 검증
     * TODO: 대용량 데이터 환경에서는 비효율적일 수 있으므로 추후 개선 필요
     * */
    public boolean isRecordKeyOwnedByMember(Long memberId, String recordKey) {
        return healthDataRawRepository.existsByMemberIdAndRecordKey(memberId, recordKey);
    }


    /**
     * 건강 데이터 조회 (일별 / 월별)
     * */
    @Transactional(readOnly = true)
    public List<?> getHealthData(Long memberId, String recordKey, PeriodType period, LocalDate startDate, LocalDate endDate) {
        return switch (period) {
            case DAILY -> getDaily(memberId, recordKey, startDate, endDate);
            case MONTHLY -> getMonthly(memberId, recordKey, startDate, endDate);
        };
    }

    /**
     * 일별 데이터 조회
     * */
    private List<HealthDataDailyResponse> getDaily(Long memberId, String recordKey, LocalDate startDate, LocalDate endDate) {
        // 캐시 조회
        String cacheKey = DAILY_KEY + memberId + ":" + recordKey;
        List<HealthDataDailyResponse> cached = cacheService.getCache(cacheKey, List.class);
        if (cached != null) return cached;

        // 캐시 미스 시 DB 조회
        LocalDate start = startDate != null ? startDate : LocalDate.of(1900, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<HealthDataDailyResponse> result = healthDataDailyRepository
            .findAllByMemberIdAndRecordKeyAndRecordDateBetweenOrderByRecordDateDesc(memberId, recordKey, start, end)
            .stream()
            .map(HealthDataDailyResponse::from)
            .collect(Collectors.toList());

        // 일별 캐시 (6시간)
        cacheService.setCache(cacheKey, result, 6);
        return result;
    }

    /**
     * 월별 데이터 조회
     * */
    private List<HealthDataMonthlyResponse> getMonthly(Long memberId, String recordKey, LocalDate startDate, LocalDate endDate) {
        // 캐시 조회
        String cacheKey = MONTHLY_KEY + memberId + ":" + recordKey;
        List<HealthDataMonthlyResponse> cached = cacheService.getCache(cacheKey, List.class);
        if (cached != null) return cached;

        // 캐시 미스 시 DB 조회
        LocalDate start = startDate != null ? startDate : LocalDate.of(1900, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        List<HealthDataMonthlyResponse> result = healthDataDailyRepository.findMonthlyAggregates(memberId, recordKey, start, end);

        // 월별 캐시 (24시간)
        cacheService.setCache(cacheKey, result, 24);
        return result;
    }
}