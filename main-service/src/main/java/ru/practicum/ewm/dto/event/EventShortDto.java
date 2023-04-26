package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventShortDto {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    public EventShortDto(String annotation, Category category, Long confirmedRequest, LocalDateTime eventDate,
                         Long id, User initiator, Boolean paid, String title) {
        this.annotation = annotation;
        this.category = new CategoryDto(category.getId(), category.getName());
        this.confirmedRequests = confirmedRequest;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = new UserShortDto(initiator.getId(), initiator.getName());
        this.paid = paid;
        this.title = title;
        this.views = 0L;
    }

    public EventShortDto(Long id) {
        this.id = id;
    }
}