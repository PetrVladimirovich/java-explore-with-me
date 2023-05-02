package ru.practicum.ewm;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateUtils {
    public static final String DATE_TIME_FORMAT_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMAT_SSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime getTime() {
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
