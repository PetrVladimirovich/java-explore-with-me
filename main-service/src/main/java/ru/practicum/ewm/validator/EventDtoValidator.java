package ru.practicum.ewm.validator;

import ru.practicum.ewm.dto.event.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDtoValidator implements ConstraintValidator<EventValid, NewEventDto> {
    @Override
    public boolean isValid(NewEventDto dto, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (dto.getEventDate() != null && dto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Field: eventDate. Error: " +
                    "должно содержать дату, которая еще ненаступила. Value: " +
                    dto.getEventDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).addPropertyNode("eventDate").addConstraintViolation();
            return false;
        }
        return true;
    }
}