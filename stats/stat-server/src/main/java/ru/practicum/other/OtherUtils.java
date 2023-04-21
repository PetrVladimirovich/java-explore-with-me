package ru.practicum.other;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

import static ru.practicum.other.Constants.DATE_PATTERN;

@UtilityClass
public class OtherUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}
