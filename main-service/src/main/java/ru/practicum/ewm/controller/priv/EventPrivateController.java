package ru.practicum.ewm.controller.priv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.ReactionOnEventDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.eventupdate.UpdateEventUserRequestDto;
import ru.practicum.ewm.dto.user.UserRatingDto;
import ru.practicum.ewm.model.ReactionOnEvent;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RatingService;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMAT_SS;

@Slf4j
@Validated
@AllArgsConstructor
@Controller
@RequestMapping("/users/{userId}")
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final RatingService ratingService;
    private final UserService userService;

    @PostMapping("/events")
    public ResponseEntity<EventFullDto> createEvent(@RequestBody @Valid NewEventDto dto,
                                                    @PathVariable(name = "userId") Long userId) {
        log.info("User creation of id={}, a new event: {}", userId, dto);
        return new ResponseEntity<>(eventService.createEvent(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventWithReactionFullDto> getUserEventById(@PathVariable(name = "userId") Long userId,
                                                                     @PathVariable(name = "eventId") Long eventId) {
        log.info("Getting user id={}, event id={}", userId, eventId);
        return ResponseEntity.ok(eventService.getUserEventById(userId, eventId));
    }

    @PatchMapping("/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResultDto> updateRequestsStatuses(@RequestBody @Valid EventRequestStatusUpdateRequest dto,
                                                                                    @PathVariable(name = "userId") Long userId,
                                                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Updates by the user id={}, in the event id={}, the status of applications: {}", userId, eventId, dto);
        return ResponseEntity.ok(eventService.updateRequestsStatuses(userId, eventId, dto));
    }

    @GetMapping("/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable(name = "userId") Long userId,
                                                                          @PathVariable(name = "eventId") Long eventId) {
        log.info("Receiving user id={} requests to participate in the event id={}", userId, eventId);
        return ResponseEntity.ok(requestService.getUserEventRequests(userId, eventId));
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventWithReactionFullDto> updateEvent(@RequestBody @Valid UpdateEventUserRequestDto dto,
                                                                @PathVariable(name = "userId") Long userId,
                                                                @PathVariable(name = "eventId") Long eventId) {
        log.info("User updates id={}, events id={}", userId, eventId);
        return ResponseEntity.ok(eventService.updateEvent(userId, eventId, dto));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventFullDto>> getUserEvents(@PathVariable(name = "userId") Long userId,
                                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") EventSortingTypes sort) {
        log.info("Getting User id={} events", userId);
        return ResponseEntity.ok(eventService.getAllUserEvents(userId, from, size, sort));
    }

    @PostMapping("/events/{eventId}/reaction")
    public ResponseEntity<ReactionOnEventDto> createReactionOnEvent(@PathVariable(name = "userId") Long userId,
                                                                    @PathVariable(name = "eventId") Long eventId,
                                                                    @RequestParam(name = "react") ReactionOnEvent.ReactionStatus reactionStatus) {
        log.info("User adding id={} to event id={}, reactions: {}", userId, eventId, reactionStatus);
        return new ResponseEntity<>(ratingService.createReaction(userId, eventId, reactionStatus), HttpStatus.CREATED);
    }

    @PatchMapping("/events/{eventId}/reaction")
    public ResponseEntity<ReactionOnEventDto> updateReactionOnEvent(@PathVariable(name = "userId") Long userId,
                                                                    @PathVariable(name = "eventId") Long eventId,
                                                                    @RequestParam(name = "react") ReactionOnEvent.ReactionStatus reactionStatus) {
        log.info("User adding id={} to event id={}, reactions: {}", userId, eventId, reactionStatus);
        return ResponseEntity.ok(ratingService.updateReaction(userId, eventId, reactionStatus));
    }

    @DeleteMapping("/events/{eventId}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReactionOnEvent(@PathVariable(name = "userId") Long userId,
                                      @PathVariable(name = "eventId") Long eventId) {
        log.info("Cancel user id={}'s reaction to event id={}", userId, eventId);
        ratingService.deleteReaction(userId, eventId);
    }

    @GetMapping("/events/{eventId}/reaction")
    public ResponseEntity<List<ReactionOnEventDto>> getReactionsOnEvent(@PathVariable(name = "eventId") Long eventId,
                                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get user reactions to the event id={}", eventId);
        return ResponseEntity.ok(ratingService.getReactionsOnEvent(eventId, from, size));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<UserRatingDto>> getMostRatingUsersSinceEventPublishDate(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                                       @RequestParam(name = "eventDate", required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT_SS) LocalDateTime eventPublishedDate) {
        log.info("Get the highest rated event initiators");
        return ResponseEntity.ok(userService.getMostRatingUser(from, size, eventPublishedDate));
    }
}