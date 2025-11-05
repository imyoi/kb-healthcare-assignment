package com.kb.healthcare.myohui.domain.dto;

import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataDailyResponse {

    private String recordKey;
    private String recordDate;
    private int steps;
    private float calories;
    private float distance;

    public static HealthDataDailyResponse from(HealthDataDaily entity) {
        return new HealthDataDailyResponse(
            entity.getRecordKey(),
            entity.getRecordDate().toString(),
            entity.getTotalSteps(),
            entity.getTotalCalories(),
            entity.getTotalDistance()
        );
    }
}