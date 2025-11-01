package com.kb.healthcare.myohui.repository;

import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import com.kb.healthcare.myohui.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface HealthDataDailyRepository extends JpaRepository<HealthDataDaily, Long> {

    @Query("SELECT d FROM HealthDataDaily d WHERE d.recordKey = :recordKey AND d.recordDate IN :recordDates")
    List<HealthDataDaily> findAllByMemberAndRecordKeyAndRecordDateIn(Member member, @Param("recordKey") String recordKey, @Param("recordDates") Collection<LocalDate> recordDates);
}