package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;
    private final EventService eventService;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Compilation compilation = compilationRepository.save(mapper.toCompilation(dto));
        CompilationDto compilationDto = mapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventService.getEventsList(compilation.getEvents()));
        return compilationDto;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        if (dto.getEvents() != null) {
            Set<Event> updatedEvents = new HashSet<>();
            for (Long eventId : dto.getEvents()) {
                updatedEvents.add(eventRepository.getReferenceById(eventId));
            }
            compilation.setEvents(updatedEvents);
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (!StringUtils.isEmpty(dto.getTitle())) {
            compilation.setTitle(dto.getTitle());
        }
        compilationRepository.save(compilation);
        CompilationDto compilationDto = mapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventService.getEventsList(compilation.getEvents()));
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Compilation with id=" + compId + " was not found");
        }
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Compilation with id=%d was not found", compId)));
        CompilationDto compilationDto = mapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventService.getEventsList(compilation.getEvents()));
        return compilationDto;
    }

    @Override
    public Page<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<Compilation> compilationsPage = compilationRepository.getCompilations(pinned, page);
        List<Compilation> allCompilations = compilationsPage.getContent();

        Map<Long, List<Long>> compilationIdWithItEventsId = new HashMap<>();
        Set<Event> allEvents = new HashSet<>();
        for (Compilation compilation : allCompilations) {
            List<Long> compilationEventsId = new ArrayList<>();
            for (Event event : compilation.getEvents()) {
                allEvents.add(event);
                compilationEventsId.add(event.getId());
            }
            compilationIdWithItEventsId.put(compilation.getId(), compilationEventsId);
        }

        List<CompilationDto> compilationDtos = mapper.toCompilationDtos(allCompilations);
        List<EventShortDto> allEventDtos = eventService.getEventsList(allEvents);

        Map<Long, EventShortDto> eventIdWithItDto = allEventDtos.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));
        for (CompilationDto compilationDto : compilationDtos) {
            List<EventShortDto> compilationEventDtos = new ArrayList<>();
            for (Long idCompilation : compilationIdWithItEventsId.get(compilationDto.getId())) {
                compilationEventDtos.add(eventIdWithItDto.get(idCompilation));
            }
            compilationDto.setEvents(compilationEventDtos.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate).reversed()).collect(Collectors.toList()));
        }
        return new PageImpl<>(compilationDtos.stream()
                .sorted(Comparator.comparing(CompilationDto::getId)).collect(Collectors.toList()),
                compilationsPage.getPageable(), compilationsPage.getTotalElements());
    }
}