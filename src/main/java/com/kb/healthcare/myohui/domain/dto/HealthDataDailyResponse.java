package com.kb.healthcare.myohui.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kb.healthcare.myohui.domain.entity.HealthDataDaily;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataDailyResponse {

    @JsonProperty(value = "Daily")
    private String recordDate;

    private int steps;
    private float calories;
    private float distance;
    private String recordKey;

    public static HealthDataDailyResponse from(HealthDataDaily entity) {
        return new HealthDataDailyResponse(
            entity.getRecordDate().toString(),
            entity.getTotalSteps(),
            entity.getTotalCalories(),
            entity.getTotalDistance(),
            entity.getRecordKey()
        );
    }
}