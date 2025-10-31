package com.kb.healthcare.myohui.domain.enums;

import com.kb.healthcare.myohui.global.enums.DbEnum;
import com.kb.healthcare.myohui.global.enums.EnumConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HealthSource implements DbEnum<String> {

    SAMSUNG_HEALTH("SamsungHealth"),
    APPLE_HEALTH("AppleHealth"),
    GOOGLE_FIT("GoogleFit"),
    OTHER("Other");

    private final String dbValue;

    @Converter(autoApply = true)
    public static class HealthSourceConverter extends EnumConverter<HealthSource, String> {
        HealthSourceConverter() {
            super(HealthSource.class);
        }
    }
    public static HealthSource from(String name) {
        for (HealthSource source : values()) {
            if (source.name().equalsIgnoreCase(name) || 
                source.dbValue.equalsIgnoreCase(name)) {
                return source;
            }
        }
        return OTHER;
    }
}