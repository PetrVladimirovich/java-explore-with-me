package ru.practicum.ewm.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventWithReactionFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", source = "category.id")
    NewEventDto toNewEventDto(Event event);

    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "state", constant = "PENDING")
    Event toEvent(NewEventDto event);

    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtos(List<Event> event);

    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventFullDto toEventFullDto(Event event);

    @IterableMapping(elementTargetType = EventFullDto.class)
    List<EventFullDto> toEventFullDtos(List<Event> event);

    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventWithReactionFullDto toEventWithReactionFullDto(Event event);
}