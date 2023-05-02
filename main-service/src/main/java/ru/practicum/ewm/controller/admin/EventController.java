package ru.practicum.ewm.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequestDto;
import ru.practicum.ewm.model.EventStatus;
import ru.practicum.ewm.service.EventService;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class EventController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getUserEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                                            @RequestParam(name = "states", required = false) List<String> states,
                                                            @RequestParam(name = "categories", required = false) List<Integer> categories,
                                                            @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting User events id={}", users);
        LocalDateTime start = null;
        if (!StringUtils.isEmpty(rangeStart)) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        LocalDateTime end = null;
        if (!StringUtils.isEmpty(rangeEnd)) {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        List<EventStatus> eventStatuses = null;
        if (!CollectionUtils.isEmpty(states)) {
            eventStatuses = new ArrayList<>();
            for (String state : states) {
                eventStatuses.add(EventStatus.from(state).orElseThrow(() -> new IllegalArgumentException("Failed to convert " +
                        "value of type java.lang.String to required type EventStatus; nested exception is " +
                        "IllegalArgumentException: For input string: " + state)));
            }
        }
        Page<EventFullDto> eventsByFilters = eventService.getEventsByFilters(users, eventStatuses, categories, start, end, from,
                size, null);
        return new ResponseEntity<>(eventsByFilters.getContent(), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @NotNull UpdateEventUserRequestDto dto,
                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Event Update id={} на: {}", eventId, dto);
        return new ResponseEntity<>(eventService.updateEvent(null, eventId, dto, true), HttpStatus.OK);
    }
}