package com.kb.healthcare.myohui.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PeriodType {

    DAILY("Daily"),
    MONTHLY("Monthly");

    private final String value;
}