package ru.practicum.ewm.controller.pub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.EventSortingTypes;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@Slf4j
@Validated
@AllArgsConstructor
@Controller
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(value = "text", required = false) String text,
                                                         @RequestParam(name = "categories", required = false) List<Integer> categories,
                                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                                         @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_SS) LocalDateTime rangeStart,
                                                         @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_SS) LocalDateTime rangeEnd,
                                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                         @RequestParam(name = "sort", required = false) EventSortingTypes sort,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest request) {
        log.info("Getting events from position={}, size={}", from, size);
        eventService.addStatisticsToStatServer(request);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<EventShortDto> events = eventService.getEventsForPublicUsersWithFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        log.info("Getting an event with id={}", id);
        eventService.addStatisticsToStatServer(request);
        return ResponseEntity.ok(eventService.getPublishedEventById(id));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<EventFullDto>> getMostRatingEvents(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting the upcoming most popular events");
        return ResponseEntity.ok(eventService.getMostRatingEvents(from, size));
    }
}