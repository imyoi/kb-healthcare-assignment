package com.kb.healthcare.myohui.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final Pattern OFFSET_PATTERN = Pattern.compile("(\\+|\\-)(\\d{2})(\\d{2})");

    /**
     * @param input 파싱할 문자열 (e.g. "2024-11-14T21:20:00+0900")
     * @return LocalDateTime
     */
    public static LocalDateTime parseFlexible(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("입력된 날짜 문자열이 비어있습니다.");
        }

        String normalized = OFFSET_PATTERN.matcher(input).replaceAll("$1$2:$3");
        return OffsetDateTime.parse(normalized).toLocalDateTime();
    }
}