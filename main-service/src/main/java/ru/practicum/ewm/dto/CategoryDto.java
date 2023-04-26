package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Data
public class CategoryDto {
    private Integer id;
    @NotEmpty
    private String name;
}