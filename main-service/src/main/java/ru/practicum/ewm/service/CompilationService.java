package ru.practicum.ewm.service;

import org.springframework.data.domain.Page;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto getCompilation(Long compId);

    Page<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}