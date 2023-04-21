package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;

import static ru.practicum.other.Constants.DATE_PATTERN;

@Mapper(componentModel = "spring")
public interface StatMapper {
    @Mapping(target = "timestamp", dateFormat = DATE_PATTERN)
    Stat toStat(StatDto dto);
}