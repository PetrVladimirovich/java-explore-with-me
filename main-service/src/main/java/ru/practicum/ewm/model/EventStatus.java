package ru.practicum.ewm.model;

import java.util.Optional;

public enum EventStatus {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static Optional<EventStatus> from(String stringState) {
        for (EventStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}