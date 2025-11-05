package com.kb.healthcare.myohui.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataMonthlyResponse {

    private String recordKey;
    private String recordMonth;
    private int steps;
    private float calories;
    private float distance;

    public HealthDataMonthlyResponse(String recordKey,
                                     Number year,
                                     Number month,
                                     Number steps,
                                     Number calories,
                                     Number distance) {
        this.recordKey = recordKey;
        this.recordMonth = String.format("%04d-%02d", year.intValue(), month.intValue());
        this.steps = steps.intValue();
        this.calories = calories.floatValue();
        this.distance = distance.floatValue();
    }
}