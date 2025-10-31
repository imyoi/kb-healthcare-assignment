package com.kb.healthcare.myohui.global.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter
public class EnumConverter<X extends Enum<X> & DbEnum<Y>, Y> implements AttributeConverter<X, Y> {

    private final Class<X> clazz;

    protected EnumConverter(Class<X> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        if (attribute == null) return null;
        return attribute.getDbValue();
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        if (dbData == null) return null;
        return Stream.of(clazz.getEnumConstants())
                .filter(v -> v.getDbValue().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}