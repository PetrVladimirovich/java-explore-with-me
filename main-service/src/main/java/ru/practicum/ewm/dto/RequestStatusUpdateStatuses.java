package ru.practicum.ewm.dto;

import ru.practicum.ewm.model.RequestStatus;

public enum RequestStatusUpdateStatuses {
    CONFIRMED,
    REJECTED;

    public static RequestStatus getRequestStatus(RequestStatusUpdateStatuses status) {
        switch (status) {
            case CONFIRMED:
                return RequestStatus.CONFIRMED;
            case REJECTED:
                return RequestStatus.REJECTED;
            default:
                return null;
        }
    }
}