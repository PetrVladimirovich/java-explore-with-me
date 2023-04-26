package ru.practicum.ewm.controller.pub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/events")
public class EventPublicController {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(value = "text", required = false) String text,
                                                         @RequestParam(name = "categories", required = false) List<Integer> categories,
                                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                                         @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                         @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                         @RequestParam(name = "sort", required = false) String sort,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                         HttpServletRequest request) {
        log.info("Getting events from position={}, size={}", from, size);
        eventService.postRequestToStat(request);
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (StringUtils.isEmpty(rangeStart) && StringUtils.isEmpty(rangeEnd)) {
            start = LocalDateTime.now();
        } else if (!StringUtils.isEmpty(rangeStart)) {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        } else {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        Page<EventShortDto> events = eventService.getEventsByFiltersShortDto(text, categories, paid, start, end,
                onlyAvailable, sort, from, size);
        return new ResponseEntity<>(events.getContent(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        log.info("Getting an event with id={}", id);
        eventService.postRequestToStat(request);
        return new ResponseEntity<>(eventService.getEventBuIdShortDto(id), HttpStatus.OK);

    }
}