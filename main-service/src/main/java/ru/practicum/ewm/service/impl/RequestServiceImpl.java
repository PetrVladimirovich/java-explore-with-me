package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConstraintException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper mapper;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event with id=" +
                eventId + " was not found"));
        if (Objects.equals(event.getInitiator().getId(), userId) || event.getState() != EventStatus.PUBLISHED) {
            throw new ConstraintException("Request for event id=" + eventId + " not available");
        }
        Integer participantLimit = event.getParticipantLimit();
        if (participantLimit > 0 && participantLimit <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConstraintException("For event id=" + eventId + " member limit exceeded");
        }

        Request request = new Request();
        request.setRequester(userRepository.getReferenceById(userId));
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return mapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        List<Request> requests;
        try {
            User user = userRepository.getReferenceById(userId);
            requests = requestRepository.findByRequesterId(user.getId());
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("User with id=" + userId + " was not found", e.getCause());
        }
        return mapper.toParticipationRequestDtos(requests);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with idss=" + requestId + " was not found"));
        if (request.getStatus() != RequestStatus.CANCELED) {
            request.setStatus(RequestStatus.CANCELED);
        }
        return mapper.toParticipationRequestDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        try {
            Event event = eventRepository.getReferenceById(eventId);
            return mapper.toParticipationRequestDtos(requestRepository.getUserEventRequests(userId, event.getId()));
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Event with id=" + userId + " was not found", e.getCause());
        }
    }
}