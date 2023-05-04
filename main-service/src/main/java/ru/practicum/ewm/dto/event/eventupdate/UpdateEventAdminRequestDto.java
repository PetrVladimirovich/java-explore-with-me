package ru.practicum.ewm.dto.event.eventupdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.EventStatus;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateEventAdminRequestDto extends UpdateEventRequestDto {
    private StateActionStatus stateAction;

    public enum StateActionStatus {
        REJECT_EVENT,
        PUBLISH_EVENT;

        public static Optional<EventStatus> getEventStatus(UpdateEventAdminRequestDto.StateActionStatus status) {
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
}