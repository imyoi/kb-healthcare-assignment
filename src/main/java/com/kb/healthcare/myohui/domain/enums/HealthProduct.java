package com.kb.healthcare.myohui.domain.enums;

import com.kb.healthcare.myohui.global.enums.DbEnum;
import com.kb.healthcare.myohui.global.enums.EnumConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HealthProduct implements DbEnum<String> {

    ANDROID("Android"),
    IOS("iOS"),
    WATCH("Smart Watch"),
    UNKNOWN("Unknown");

    private final String dbValue;

    @Converter(autoApply = true)
    public static class HealthProductConverter extends EnumConverter<HealthProduct, String> {
        public HealthProductConverter() {
            super(HealthProduct.class);
        }
    }

    public static HealthProduct from(String name) {
        for (HealthProduct product : values()) {
            if (product.name().equalsIgnoreCase(name) ||
                product.dbValue.equalsIgnoreCase(name)) {
                return product;
            }
        }
        return UNKNOWN;
    }
}