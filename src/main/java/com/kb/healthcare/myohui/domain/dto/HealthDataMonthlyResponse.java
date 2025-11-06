package com.kb.healthcare.myohui.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataMonthlyResponse {

    @JsonProperty("Monthly")
    private String recordMonth;

    private int steps;
    private float calories;
    private float distance;
    private String recordKey;

    public HealthDataMonthlyResponse(String recordKey,
                                     Number year,
                                     Number month,
                                     Number steps,
                                     Number calories,
                                     Number distance) {
        this.recordMonth = String.format("%04d-%02d", year.intValue(), month.intValue());
        this.steps = steps.intValue();
        this.calories = calories.floatValue();
        this.distance = distance.floatValue();
        this.recordKey = recordKey;
    }
}