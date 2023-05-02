package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.LocationDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateEventUserRequestDto {
    @Length(min = 20, max = 2000)
    private String annotation;
    @Positive
    private Integer category;
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT_SS)
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionStatusDto stateAction;
    @Length(min = 3, max = 120)
    private String title;
}