package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatCountDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatService;
import static ru.practicum.other.OtherUtils.getFormatter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus
    public void createStat(@RequestBody @NotNull @Valid StatDto dto) {
        log.info("Write in statistics {}", dto);
        service.createStat(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatCountDto>> getStats(@RequestParam(name = "start") String start,
                                                       @RequestParam(name = "end") String end,
                                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique,
                                                       @RequestParam(name = "uris", required = false) List<String> uris) {
        log.info("Getting statistics: with = {}, by = {}, unique ip = {}, filter = {}", start, end, unique, uris);

        return new ResponseEntity<>(service.getStats(LocalDateTime.parse(start, getFormatter()),
                LocalDateTime.parse(end, getFormatter()), unique, uris), HttpStatus.OK);
    }
}