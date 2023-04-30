package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;
import static ru.practicum.ewm.DateUtils.getTime;

@Data
@NoArgsConstructor
public class ApiErrorDto {
    private String status;
    private String reason;
    private String message;
    @JsonFormat(pattern = DATE_TIME_FORMAT_SS)
    private LocalDateTime timestamp;

    public ApiErrorDto(String status, String reason, String message) {
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.timestamp = getTime();
    }
}