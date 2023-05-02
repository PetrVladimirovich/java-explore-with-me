package ru.practicum.ewm.controller.priv;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable(name = "userId") Long userId,
                                                                 @RequestParam(name = "eventId", required = true) Long eventId) {
        log.info("Adding a request to participate in the event id={}, user id={}", eventId, userId);
        return new ResponseEntity(service.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable(name = "userId") Long userId) {
        log.info("Getting all user requests id={}", userId);
        return new ResponseEntity(service.getRequests(userId), HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable(name = "userId") Long userId,
                                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("Cancellation of request id={}, user id={}", requestId, userId);
        return new ResponseEntity(service.cancelRequest(userId, requestId), HttpStatus.OK);
    }
}