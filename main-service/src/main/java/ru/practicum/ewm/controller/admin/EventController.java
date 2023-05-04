package ru.practicum.ewm.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventSortingTypes;
import ru.practicum.ewm.dto.event.eventupdate.UpdateEventAdminRequestDto;
import ru.practicum.ewm.model.EventStatus;
import ru.practicum.ewm.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@Slf4j
@Validated
@AllArgsConstructor
@Controller
@RequestMapping("/admin/events")
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getUserEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                                            @RequestParam(name = "states", required = false) List<String> states,
                                                            @RequestParam(name = "categories", required = false) List<Integer> categories,
                                                            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_SS) LocalDateTime rangeStart,
                                                            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_SS) LocalDateTime rangeEnd,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") EventSortingTypes sort) {
        log.info("Getting User events id={}", users);
        List<EventStatus> eventStatuses = null;
        if (!CollectionUtils.isEmpty(states)) {
            eventStatuses = new ArrayList<>();
            for (String state : states) {
                eventStatuses.add(EventStatus.from(state).orElseThrow(() -> new IllegalArgumentException("Failed to convert " +
                        "value of type java.lang.String to required type EventStatus; nested exception is " +
                        "IllegalArgumentException: For input string: " + state)));
            }
        }
        List<EventFullDto> eventsByFilters = eventService.getEventsForPrivateUsersWithFilters(users, eventStatuses, categories, rangeStart, rangeEnd, from,
                size, null, sort);
        return ResponseEntity.ok(eventsByFilters);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody UpdateEventAdminRequestDto dto,
                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Event Update id={} on: {}", eventId, dto);
        return ResponseEntity.ok(eventService.updateEvent(null, eventId, dto));
    }
}