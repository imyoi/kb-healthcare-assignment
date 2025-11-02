package com.kb.healthcare.myohui.service;

import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import com.kb.healthcare.myohui.domain.entity.Member;
import com.kb.healthcare.myohui.repository.HealthDataDailyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDataAggregator {

    private final HealthDataDailyRepository healthDataDailyRepository;

    /**
     * 일별 건강 데이터 집계
     */
    @Async("healthDataExecutor")
    @Transactional
    public void aggregateDailyAsync(Member member, String recordKey, List<HealthDataRaw> raws) {
        log.info("[HealthDataAggregator] thread={}", Thread.currentThread().getName());
        try {
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
            log.info("[HealthDataAggregator] 일별 집계 완료 - memberId={}, recordKey={}, dates={}",
                member.getId(), recordKey, recordDates);
        } catch (Exception e) {
            log.error("[HealthDataAggregator] 일별 집계 실패 - memberId={}, recordKey={}, message={}",
                member.getId(), recordKey, e.getMessage(), e);
        }
    }
}