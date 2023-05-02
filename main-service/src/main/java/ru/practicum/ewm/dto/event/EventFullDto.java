package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.EventStatus;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMATTER;
import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@Data
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT_SS)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private EventStatus state;
    private String title;
    private Long views;

    public EventFullDto(String annotation, Category category, Long confirmedRequest, LocalDateTime createdOn, String description,
                        LocalDateTime eventDate, Long id, User initiator, Float lat, Float lon, Boolean paid, Integer participantLimit,
                        LocalDateTime publishedOn, Boolean requestModeration, EventStatus state, String title) {
        this.annotation = annotation;
        this.category = new CategoryDto(category.getId(), category.getName());
        this.confirmedRequests = Math.toIntExact(confirmedRequest);
        this.createdOn = createdOn.format(DATE_TIME_FORMATTER);
        this.description = description;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = new UserShortDto(initiator.getId(), initiator.getName());
        this.location = new LocationDto(lat, lon);
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn == null ? null : publishedOn.format(DATE_TIME_FORMATTER);
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = 0L;
    }

    public EventFullDto(Long id) {
        this.id = id;
    }
}