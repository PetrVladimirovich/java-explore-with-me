package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.model.EventStatus;

import java.util.Optional;

public enum AdminStateActionStatusDto {
    REJECT_EVENT,
    PUBLISH_EVENT;

    public static Optional<EventStatus> getEventStatus(AdminStateActionStatusDto status) {
        switch (status) {
            case REJECT_EVENT:
                return Optional.of(EventStatus.CANCELED);
            case PUBLISH_EVENT:
                return Optional.of(EventStatus.PUBLISHED);
            default:
                return Optional.empty();
        }
    }
}