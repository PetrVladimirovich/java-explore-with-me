package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEventDto {
    private Long id;
    private User initiator;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private Integer participants;
}