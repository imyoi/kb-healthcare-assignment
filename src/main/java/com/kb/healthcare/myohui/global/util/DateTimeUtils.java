package com.kb.healthcare.myohui.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtils {

    private static final DateTimeFormatter BASIC_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        try {
            // ISO Offset (2024-12-16T22:40:00+09:00)
            return OffsetDateTime.parse(normalized).toLocalDateTime();
        } catch (DateTimeParseException e1) {
            try {
                // 공백 구분 (2024-11-15 00:00:00)
                return LocalDateTime.parse(normalized, BASIC_FORMATTER);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("지원되지 않는 날짜 형식: " + input);
            }
        }
    }
}