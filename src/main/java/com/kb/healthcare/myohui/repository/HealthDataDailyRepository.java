package com.kb.healthcare.myohui.repository;

import com.kb.healthcare.myohui.domain.dto.HealthDataMonthlyResponse;
import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import com.kb.healthcare.myohui.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface HealthDataDailyRepository extends JpaRepository<HealthDataDaily, Long> {

    List<HealthDataDaily> findAllByMemberIdAndRecordKeyAndRecordDateBetweenOrderByRecordDateDesc(
        Long memberId, String recordKey, LocalDate startDate, LocalDate endDate
    );

    @Query("SELECT d FROM HealthDataDaily d WHERE d.recordKey = :recordKey AND d.recordDate IN :recordDates")
    List<HealthDataDaily> findAllByMemberAndRecordKeyAndRecordDateIn(
        Member member,
        @Param("recordKey") String recordKey,
        @Param("recordDates") Collection<LocalDate> recordDates
    );

    @Query("""
    SELECT new com.kb.healthcare.myohui.domain.dto.HealthDataMonthlyResponse(
        d.recordKey,
        YEAR(d.recordDate),
        MONTH(d.recordDate),
        SUM(d.totalSteps),
        SUM(d.totalCalories),
        SUM(d.totalDistance)
    )
    FROM HealthDataDaily d
    WHERE d.member.id = :memberId AND d.recordKey = :recordKey
      AND (:startDate IS NULL OR d.recordDate >= :startDate)
      AND (:endDate IS NULL OR d.recordDate <= :endDate)
    GROUP BY d.recordKey, YEAR(d.recordDate), MONTH(d.recordDate)
    ORDER BY YEAR(d.recordDate) DESC, MONTH(d.recordDate) DESC
    """)
    List<HealthDataMonthlyResponse> findMonthlyAggregates(
        @Param("memberId") Long memberId,
        @Param("recordKey") String recordKey,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}