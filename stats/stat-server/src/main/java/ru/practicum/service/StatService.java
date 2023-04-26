package ru.practicum.service;

import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatCountDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void createStat(StatDto stat);

    List<StatCountDto> getStats(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris);
}