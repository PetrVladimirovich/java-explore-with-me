package ru.practicum.ewm.service;

import org.springframework.data.domain.Page;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventFullDto createEvent(NewEventDto dto, Long serId);

    List<EventShortDto> getEventsList(Set<Event> events);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateRequestsStatuses(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto dto, Boolean adminStatus);

    Page<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size);

    Page<EventFullDto> getEventsByFilters(List<Long> userIds, List<EventStatus> eventStatus, List<Integer> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size,
                                          Long eventId);

    Page<EventShortDto> getEventsByFiltersShortDto(String text, List<Integer> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                                   String sort, Integer from, Integer size);

    EventFullDto getEventBuIdShortDto(Long id);

    void postRequestToStat(HttpServletRequest request);
}