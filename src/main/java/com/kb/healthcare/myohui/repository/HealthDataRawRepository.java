package com.kb.healthcare.myohui.repository;

import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import com.kb.healthcare.myohui.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthDataRawRepository extends JpaRepository<HealthDataRaw, Long> {

    boolean existsByMemberIdAndRecordKey(Long memberId, String recordKey);

    List<HealthDataRaw> findAllByRecordKeyAndMember(String recordKey, Member member);
}