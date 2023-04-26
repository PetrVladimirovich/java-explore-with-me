package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventStatus;

import java.time.LocalDateTime;
import java.util.*;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select new ru.practicum.ewm.dto.event.EventShortDto(a.annotation, a.category, count(r), a.eventDate, " +
            "a.id, a.initiator, a.paid, a.title) from Event a " +
            "left join Request r on r.event.id = a.id and (r.status = 'CONFIRMED' or a.participantLimit = 0) " +
            "where a.id in (?1) " +
            "group by a " +
            "order by a.eventDate asc"
    )
    List<EventShortDto> getEventsWithConfirmedRequestShortView(Set<Long> eventsId);

    @Query("select new ru.practicum.ewm.dto.event.EventFullDto(a.annotation, a.category, count(r), a.createdOn, " +
            "a.description, a.eventDate, a.id, a.initiator, a.lat, a.lon, a.paid, a.participantLimit, a.publishedOn, " +
            "a.requestModeration, a.state, a.title) from Event a " +
            "left join Request r on r.event.id = a.id and (r.status = 'CONFIRMED' or a.participantLimit = 0) " +
            "where a.id in (?1) and (?2 = null or a.initiator.id = ?2) " +
            "group by a "
    )
    List<EventFullDto> getEventsWithConfirmedRequestFullView(List<Long> eventsId, Long userId);

    @Query("select new ru.practicum.ewm.dto.event.EventFullDto(a.annotation, a.category, count(r), a.createdOn, " +
            "a.description, a.eventDate, a.id, a.initiator, a.lat, a.lon, a.paid, a.participantLimit, a.publishedOn, " +
            "a.requestModeration, a.state, a.title) from Event a " +
            "left join Request r on r.event.id = a.id and (r.status = 'CONFIRMED' or a.participantLimit = 0) " +
            "where (:#{#userIds == null} = true or a.initiator.id in :userIds) " +
            "and (:#{#categories == null} = true or a.category.id in :categories) " +
            "and (:#{#eventStatuses == null} = true or a.state in :eventStatuses) " +
            "and ( " +
            "(:#{#rangeStart != null} = true and :#{#rangeEnd != null} = true and a.eventDate between :rangeStart and :rangeEnd) " +
            "or ( " +
            "(:#{#rangeStart == null} = true or a.eventDate >= :rangeStart) and (:#{#rangeEnd == null} = true or a.eventDate <= :rangeEnd)) " +
            ") " +
            "and (:#{#eventId == null} = true or a.id = :eventId) " +
            "group by a " +
            "order by a.eventDate DESC"
    )
    Page<EventFullDto> getAllUserEvents(@Param("userIds") List<Long> userIds, @Param("eventStatuses") List<EventStatus> eventStatuses,
                                        @Param("categories") List<Integer> categories, @Param("rangeStart") LocalDateTime rangeStart,
                                        @Param("rangeEnd") LocalDateTime rangeEnd, @Param("eventId") Long eventId, Pageable page);


    @Query("select new ru.practicum.ewm.dto.event.EventShortDto(a.annotation, a.category, count(r), a.eventDate, " +
            "a.id, a.initiator, a.paid, a.title) from Event a " +
            "left join Request r on r.event.id = a.id and (r.status = 'CONFIRMED' or a.participantLimit = 0) " +
            "where a.state = 'PUBLISHED' " +
            "and (:#{#categories == null} = true or a.category.id in :categories) " +
            "and (:#{#texts == null} = true " +
            "or (lower(a.annotation) like concat('%', :texts, '%') or lower(a.description) like concat('%', :texts, '%')) " +
            ") " +
            "and (:#{#paid == null} = true or a.paid = :paid) " +
            "and ( " +
            "(:#{#rangeStart != null} = true and :#{#rangeEnd != null} = true and a.eventDate between :rangeStart and :rangeEnd) " +
            "or ( " +
            "(:#{#rangeStart == null} = true or a.eventDate >= :rangeStart) and (:#{#rangeEnd == null} = true or a.eventDate <= :rangeEnd)) " +
            ") " +
            "group by a " +
            "having :#{#onlyAvailable == false} = true or count(r) < a.participantLimit"
    )
    Page<EventShortDto> getEventsShortView(@Param("texts") String texts, @Param("categories") List<Integer> categories,
                                           @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                           @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable,
                                           Pageable page);
}