package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.validator.EventValid;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EventValid
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    private String annotation;
    @Positive
    private int category;
    @NotBlank
    @Length(min = 20, max = 7000)
    private String description;
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMAT_SS)
    private LocalDateTime eventDate;
    @NotNull
    @Valid
    private LocationDto location;
    private boolean paid = false;
    @PositiveOrZero
    private Integer participantLimit = 0;
    private boolean requestModeration = true;
    @NotBlank
    @Length(min = 3, max = 120)
    private String title;
}