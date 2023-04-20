package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class StatDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    private String ip;
    @NotBlank
    private String timestamp;
}