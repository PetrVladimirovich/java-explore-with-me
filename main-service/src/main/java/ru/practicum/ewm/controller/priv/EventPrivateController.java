package ru.practicum.ewm.controller.priv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@RequestBody @NotNull @Valid NewEventDto dto,
                                                    @PathVariable(name = "userId") Long userId) {
        log.info("User creation id={}, new event: {}", userId, dto);
        return new ResponseEntity<>(eventService.createEvent(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEventById(@PathVariable(name = "userId") Long userId,
                                                         @PathVariable(name = "eventId") Long eventId) {
        log.info("Getting user id={}, event id={}", userId, eventId);
        return new ResponseEntity<>(eventService.getUserEventById(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResultDto> updateRequestsStatuses(@RequestBody @NotNull @Valid EventRequestStatusUpdateRequest dto,
                                                                                    @PathVariable(name = "userId") Long userId,
                                                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Updates by the user id={}, in the event id={}, the status of applications: {}", userId, eventId, dto);
        return new ResponseEntity<>(eventService.updateRequestsStatuses(userId, eventId, dto), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable(name = "userId") Long userId,
                                                                          @PathVariable(name = "eventId") Long eventId) {
        log.info("Receiving user id={} requests to participate in the event id={}", userId, eventId);
        return new ResponseEntity<>(requestService.getUserEventRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@RequestBody @NotNull @Valid UpdateEventUserRequestDto dto,
                                                    @PathVariable(name = "userId") Long userId,
                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("User updates id={}, events id={}", userId, eventId);
        return new ResponseEntity<>(eventService.updateEvent(userId, eventId, dto, false), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getUserEvents(@PathVariable(name = "userId") Long userId,
                                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting User id={} events", userId);
        Page<EventFullDto> allUserEvents = eventService.getAllUserEvents(userId, from, size);
        return new ResponseEntity<>(allUserEvents.getContent(), HttpStatus.OK);
    }
}