package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.ReactionOnEvent;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<ReactionOnEvent, Long> {
    Optional<ReactionOnEvent> findByParticipantIdAndEventId(Long userId, Long eventId);

    List<ReactionOnEvent> findFirst3ByEventIdOrderByTimestampDesc(Long eventId);

    Page<ReactionOnEvent> findByEventId(Long eventId, Pageable page);

    List<ReactionOnEvent> findByParticipantIdAndEventIdIn(Long userId, List<Long> eventsId);
}