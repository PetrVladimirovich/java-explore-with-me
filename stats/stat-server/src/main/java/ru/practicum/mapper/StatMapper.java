package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;

@Mapper(componentModel = "spring")
public interface StatMapper {
    @Mapping(target = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    Stat toStat(StatDto dto);
}