package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ReactionOnEventDto;
import ru.practicum.ewm.model.ReactionOnEvent;
import ru.practicum.ewm.model.Request;

import java.util.List;

@Mapper
public interface RatingMapper {
    @Mapping(target = "eventId", source = "event.id")
    ReactionOnEventDto toReactionOnEventDto(ReactionOnEvent reaction);

    List<ReactionOnEventDto> toReactionOnEventDtos(List<ReactionOnEvent> reaction);

    @Mapping(target = "participant", source = "request.requester")
    @Mapping(target = "reaction", source = "reactionStatus")
    @Mapping(target = "event", source = "request.event")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    ReactionOnEvent toReactionOnEvent(Request request, ReactionOnEvent.ReactionStatus reactionStatus);
}