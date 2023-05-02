package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("update Request r " +
            "set r.status = ?2 " +
            "where r.event.id = ?1 and r.status = ?3"
    )
    void setCanceledStatusForRequestByEventId(Long eventId, RequestStatus requestStatus, RequestStatus pendingStatus);

    @Query("select r " +
            "from Request r " +
            "where r.event.id = ?2 and r.event.initiator.id = ?1"

    )
    List<Request> getUserEventRequests(Long userId, Long eventId);
}