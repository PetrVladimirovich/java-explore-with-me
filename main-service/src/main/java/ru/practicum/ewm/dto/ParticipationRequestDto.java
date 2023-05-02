package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SSS;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipationRequestDto {
    @JsonFormat(pattern = DATE_TIME_FORMAT_SSS)
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}