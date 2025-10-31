package com.kb.healthcare.myohui.repository;

import com.kb.healthcare.myohui.domain.entity.HealthDataRaw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthDataRepository extends JpaRepository<HealthDataRaw, Long> {
}