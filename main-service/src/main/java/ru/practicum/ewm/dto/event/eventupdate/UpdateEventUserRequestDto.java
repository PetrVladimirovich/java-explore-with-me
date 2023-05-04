package ru.practicum.ewm.dto.event.eventupdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.EventStatus;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateEventUserRequestDto extends UpdateEventRequestDto {
    private StateActionStatus stateAction;

    public enum StateActionStatus {
        SEND_TO_REVIEW,
        CANCEL_REVIEW;

        public static Optional<EventStatus> getEventStatus(StateActionStatus status) {
            switch (status) {
                case SEND_TO_REVIEW:
                    return Optional.of(EventStatus.PENDING);
                case CANCEL_REVIEW:
                    return Optional.of(EventStatus.CANCELED);
                default:
                    return Optional.empty();
            }
        }
    }
}