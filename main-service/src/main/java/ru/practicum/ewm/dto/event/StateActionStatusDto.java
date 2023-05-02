package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.model.EventStatus;

import java.util.Optional;

public enum StateActionStatusDto {
    SEND_TO_REVIEW,
    CANCEL_REVIEW,
    REJECT_EVENT,
    PUBLISH_EVENT;

    public static Optional<EventStatus> getEventStatus(StateActionStatusDto status) {
        switch (status) {
            case SEND_TO_REVIEW:
                return Optional.of(EventStatus.PENDING);
            case CANCEL_REVIEW:
            case REJECT_EVENT:
                return Optional.of(EventStatus.CANCELED);
            case PUBLISH_EVENT:
                return Optional.of(EventStatus.PUBLISHED);
            default:
                return Optional.empty();
        }
    }
}