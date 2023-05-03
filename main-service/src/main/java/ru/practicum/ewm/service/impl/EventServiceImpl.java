package ru.practicum.ewm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.dto.StatCountDto;
import ru.practicum.dto.StatDto;
import ru.practicum.ewm.StatRestClient;
import ru.practicum.ewm.dto.RequestStatusUpdateStatuses;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.eventupdate.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.event.eventupdate.UpdateEventRequestDto;
import ru.practicum.ewm.dto.event.eventupdate.UpdateEventUserRequestDto;
import ru.practicum.ewm.exception.ConstraintException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RatingMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.DateUtils.DATE_TIME_FORMATTER;

@Service
public class EventServiceImpl implements EventService {
    private static final String EVENT_PATH = "/events/";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper;
    private final StatRestClient restClient;
    private final ObjectMapper objectMapper;
    private final RequestMapper requestMapper;
    private final String applicationName;
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, RequestRepository requestRepository,
                            CategoryRepository categoryRepository, EventMapper mapper, StatRestClient restClient,
                            ObjectMapper objectMapper, RequestMapper requestMapper, @Value("${spring.application.name}") String appName,
                            RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.requestMapper = requestMapper;
        this.applicationName = appName;
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }

    @Override
    public EventFullDto createEvent(NewEventDto dto, Long userId) {
        Event event = mapper.toEvent(dto);
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setCategory(categoryRepository.getReferenceById(event.getCategory().getId()));
        return mapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsList(Set<Event> eventsForGenerationDto) {
        if (eventsForGenerationDto.size() > 0) {
            Set<Long> eventsId = eventsForGenerationDto.stream().map(Event::getId).collect(Collectors.toSet());
            List<Event> events = eventRepository.getEventsFromSet(eventsId);
            List<EventShortDto> eventShortDtos = mapper.toEventShortDtos(events);
            Map<Long, Long> eventViews = getEventViews(events);
            if (eventViews.size() > 0) {
                for (EventShortDto dto : eventShortDtos) {
                    dto.setViews(eventViews.getOrDefault(dto.getId(), 0L));
                }
            }
            return eventShortDtos;
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getMostRatingEvents(Integer from, Integer size) {
        Sort sortByRating = Sort.by(Sort.Direction.DESC, "rating");
        Pageable page = PageRequest.of(from / size, size, sortByRating);
        Page<Event> eventsPage = eventRepository.findByEventDateGreaterThanAndState(LocalDateTime.now(), EventStatus.PUBLISHED, page);
        List<Event> events = eventsPage.getContent();
        if (events.size() > 0) {
            List<EventFullDto> eventFullDtos = mapper.toEventFullDtos(events);
            Map<Long, Long> eventViews = getEventViews(events);
            if (eventViews.size() > 0) {
                for (EventFullDto dto : eventFullDtos) {
                    dto.setViews(eventViews.getOrDefault(dto.getId(), 0L));
                }
            }
            return eventFullDtos;
        }
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    @Override
    public EventWithReactionFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        EventWithReactionFullDto eventWithReactionFullDto = mapper.toEventWithReactionFullDto(event);
        Map<Long, Long> eventViews = getEventViews(List.of(event));
        if (eventViews.containsKey(eventWithReactionFullDto.getId())) {
            eventWithReactionFullDto.setViews(eventViews.get(eventWithReactionFullDto.getId()));
        }
        List<ReactionOnEvent> reactionsOnEvent = ratingRepository.findFirst3ByEventIdOrderByTimestampDesc(eventId);
        eventWithReactionFullDto.setUserReactions(ratingMapper.toReactionOnEventDtos(reactionsOnEvent));
        return eventWithReactionFullDto;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestsStatuses(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (CollectionUtils.isEmpty(dto.getRequestIds()) || dto.getStatus() == null) {
            return new EventRequestStatusUpdateResultDto();
        }
        if (dto.getStatus() == RequestStatusUpdateStatuses.CONFIRMED) {
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                return new EventRequestStatusUpdateResultDto();
            }
            if (Objects.equals(Long.valueOf(event.getParticipantLimit()), event.getConfirmedRequests())) {
                throw new ConstraintException("The participant limit has been reached");
            }
        }
        List<Request> requests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
        List<Request> requestsForUpdate = requests.stream()
                .filter(request -> dto.getRequestIds().contains(request.getId())).collect(Collectors.toList());
        if (dto.getStatus() == RequestStatusUpdateStatuses.CONFIRMED &&
                (requestsForUpdate.size() + event.getConfirmedRequests()) > event.getParticipantLimit()) {
            throw new ConstraintException("The participant limit has been reached");
        }
        if (requestsForUpdate.size() != dto.getRequestIds().size()) {
            throw new ConstraintException("Request must have status PENDING");
        }
        for (Request request : requestsForUpdate) {
            request.setStatus(RequestStatusUpdateStatuses.getRequestStatus(dto.getStatus()));
        }
        requestRepository.saveAll(requestsForUpdate);
        EventRequestStatusUpdateResultDto result = new EventRequestStatusUpdateResultDto();
        if (dto.getStatus() == RequestStatusUpdateStatuses.CONFIRMED) {
            result.setConfirmedRequests(requestMapper.toParticipationRequestDtos(requestsForUpdate));
            if ((requestsForUpdate.size() + event.getConfirmedRequests()) == event.getParticipantLimit()) {
                List<Request> requestsForReject = requests.stream()
                        .filter(request -> !dto.getRequestIds().contains(request.getId())).collect(Collectors.toList());
                for (Request request : requestsForReject) {
                    request.setStatus(RequestStatus.REJECTED);
                }
                requestRepository.saveAll(requestsForReject);
                result.setRejectedRequests(requestMapper.toParticipationRequestDtos(requestsForReject));
            }
        }
        if (dto.getStatus() == RequestStatusUpdateStatuses.REJECTED) {
            result.setRejectedRequests(requestMapper.toParticipationRequestDtos(requestsForUpdate));
        }
        return result;
    }

    @Override
    @Transactional
    public EventWithReactionFullDto updateEvent(Long userId, Long eventId, UpdateEventRequestDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (dto.getEventDate() != null && dto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new ConstraintException("Field: eventDate. Error: должно содержать дату, которая еще ненаступила. " +
                    "Value: " + dto.getEventDate());
        }
        if (dto instanceof UpdateEventUserRequestDto) {
            checkEventParametersWhenUserUpdateIt(event, (UpdateEventUserRequestDto) dto, userId);
        }
        if (dto instanceof UpdateEventAdminRequestDto) {
            checkEventParametersWhenAdminUpdateIt(event, (UpdateEventAdminRequestDto) dto);
        }
        if (!StringUtils.isBlank(dto.getAnnotation())) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryRepository.getReferenceById(dto.getCategory()));
        }
        if (!StringUtils.isBlank(dto.getDescription())) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            if (dto.getLocation().getLat() != null) {
                event.setLat(dto.getLocation().getLat());
            }
            if (dto.getLocation().getLon() != null) {
                event.setLon(dto.getLocation().getLon());
            }
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto instanceof UpdateEventUserRequestDto) {
            if (((UpdateEventUserRequestDto) dto).getStateAction() != null) {
                UpdateEventUserRequestDto.StateActionStatus stateActionStatus = ((UpdateEventUserRequestDto) dto).getStateAction();
                UpdateEventUserRequestDto.StateActionStatus.getEventStatus(stateActionStatus)
                        .ifPresent(event::setState);
            }
        }
        if (dto instanceof UpdateEventAdminRequestDto) {
            if (((UpdateEventAdminRequestDto) dto).getStateAction() != null) {
                UpdateEventAdminRequestDto.StateActionStatus stateActionStatus = ((UpdateEventAdminRequestDto) dto).getStateAction();
                UpdateEventAdminRequestDto.StateActionStatus.getEventStatus(stateActionStatus)
                        .ifPresent(status -> {
                            event.setState(status);
                            if (status == EventStatus.PUBLISHED) {
                                event.setPublishedOn(LocalDateTime.now());
                            }
                        });
            }
        }
        if (!StringUtils.isBlank(dto.getTitle())) {
            event.setTitle(dto.getTitle());
        }
        EventWithReactionFullDto eventWithReactionFullDto = mapper.toEventWithReactionFullDto(event);
        Map<Long, Long> eventViews = getEventViews(List.of(event));
        if (eventViews.containsKey(eventWithReactionFullDto.getId())) {
            eventWithReactionFullDto.setViews(eventViews.get(eventWithReactionFullDto.getId()));
        }
        List<ReactionOnEvent> reactionsOnEvent = ratingRepository.findFirst3ByEventIdOrderByTimestampDesc(eventId);
        eventWithReactionFullDto.setUserReactions(ratingMapper.toReactionOnEventDtos(reactionsOnEvent));
        return eventWithReactionFullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size, EventSortingTypes sort) {
        List<EventFullDto> eventDtos = getEventsForPrivateUsersWithFilters(List.of(userId), null, null, null, null, from, size, null, sort);
        return eventDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getEventsForPrivateUsersWithFilters(List<Long> userIds, List<EventStatus> eventStatus,
                                                                  List<Integer> categories, LocalDateTime rangeStart,
                                                                  LocalDateTime rangeEnd, Integer from, Integer size,
                                                                  Long eventId, EventSortingTypes sort) {
        Sort sortBy;
        if (sort == EventSortingTypes.RATING) {
            sortBy = Sort.by(Sort.Direction.DESC, "rating");
        } else {
            sortBy = Sort.by(Sort.Direction.DESC, "eventDate");
        }
        Pageable page = PageRequest.of(from / size, size, sortBy);
        Page<Event> eventsPage = eventRepository.getEventsForPrivateUsers(userIds, eventStatus, categories, rangeStart,
                rangeEnd, eventId, page);
        List<Event> events = eventsPage.getContent();
        List<EventFullDto> eventFullDtos = mapper.toEventFullDtos(events);
        Map<Long, Long> eventViews = getEventViews(events);
        if (eventViews.size() > 0) {
            for (EventFullDto dto : eventFullDtos) {
                dto.setViews(eventViews.getOrDefault(dto.getId(), 0L));
            }
        }
        return eventFullDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsForPublicUsersWithFilters(String text, List<Integer> categories, Boolean paid,
                                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                                  Boolean onlyAvailable, EventSortingTypes sort,
                                                                  Integer from, Integer size) {
        if (!StringUtils.isBlank(text)) {
            text = text.trim().toLowerCase();
        }
        Sort sortByEventDate = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from / size, size, sortByEventDate);
        Page<Event> eventPage = eventRepository.getEventsForPublicUsers(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, page);
        List<Event> events = eventPage.getContent();
        List<EventShortDto> eventShortDtos = mapper.toEventShortDtos(events);
        Map<Long, Long> eventViews = getEventViews(events);
        if (eventViews.size() > 0) {
            for (EventShortDto dto : eventShortDtos) {
                dto.setViews(eventViews.getOrDefault(dto.getId(), 0L));
            }
        }

        Comparator<EventShortDto> comparing = Comparator.comparing(EventShortDto::getId);
        if (sort != null)
            switch (sort) {
                case EVENT_DATE:
                    comparing = Comparator.comparing(EventShortDto::getEventDate);
                    break;
                case VIEWS:
                    comparing = Comparator.comparing(EventShortDto::getViews);
                    break;
            }
        return eventShortDtos.stream().sorted(comparing).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getPublishedEventById(Long eventId) {
        Event event = eventRepository.findByIdAndState(eventId, EventStatus.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
        EventFullDto eventFullDto = mapper.toEventFullDto(event);
        Map<Long, Long> eventViews = getEventViews(List.of(event));
        if (eventViews.containsKey(eventFullDto.getId())) {
            eventFullDto.setViews(eventViews.get(eventFullDto.getId()));
        }
        return eventFullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public void addStatisticsToStatServer(HttpServletRequest request) {
        StatDto statDto = new StatDto(applicationName, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER));
        restClient.post(statDto);
    }

    private void checkEventParametersWhenUserUpdateIt(Event event, UpdateEventUserRequestDto dto, Long userId) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ObjectNotFoundException(String.format("Event with id=%d was not available", event.getId()));
        }
        if (event.getState() == EventStatus.PUBLISHED) {
            throw new ConstraintException("Only pending or canceled events can be changed");
        }
    }

    private void checkEventParametersWhenAdminUpdateIt(Event event, UpdateEventAdminRequestDto dto) {
        if (dto.getStateAction() != null) {
            if (dto.getStateAction() == UpdateEventAdminRequestDto.StateActionStatus.PUBLISH_EVENT &&
                    event.getState() != EventStatus.PENDING) {
                throw new ConstraintException("Cannot publish the event because it's not in the right state: PUBLISHED");
            }
            if (dto.getStateAction() == UpdateEventAdminRequestDto.StateActionStatus.REJECT_EVENT &&
                    event.getState() == EventStatus.PUBLISHED) {
                throw new ConstraintException("Cannot reject the event because it's not in the right state: PUBLISHED");
            }
        }
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        Set<Long> eventsId = events.stream().map(Event::getId).collect(Collectors.toSet());
        LocalDateTime startCountEventViews = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo).orElseGet(LocalDateTime::now);
        return getStatsFromStatServer(startCountEventViews, LocalDateTime.now(), true, eventsId);
    }

    public Map<Long, Long> getStatsFromStatServer(LocalDateTime start, LocalDateTime end, Boolean unique, Set<Long> uris) {
        StringBuilder builder = new StringBuilder();
        for (Long uri : uris) {
            builder.append(EVENT_PATH + uri + ",");
        }
        ResponseEntity<Object[]> response = restClient.get(start, end, unique, builder.toString());
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Object[] object = response.getBody();
            List<StatCountDto> collect = Arrays.stream(object).map(o -> objectMapper.convertValue(o, StatCountDto.class))
                    .filter(dto -> dto.getApp().equals(applicationName))
                    .collect(Collectors.toList());
            Map<Long, Long> resultMap = new HashMap<>();
            for (StatCountDto dto : collect) {
                Long id = NumberUtils.toLong(StringUtils.substringAfterLast(dto.getUri(), "/"));
                if (id > 0) {
                    resultMap.put(id, dto.getHits());
                }
            }
            return resultMap;
        } else {
            return Collections.emptyMap();
        }
    }
}