package ru.practicum.ewm.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.ReactionOnEventDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventWithReactionFullDto extends EventFullDto {
    private List<ReactionOnEventDto> userReactions;
}