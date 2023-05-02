package ru.practicum.ewm.controller.pub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService service;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable("compId") Long compId) {
        log.info("Getting a collection id={}", compId);
        return new ResponseEntity<>(service.getCompilation(compId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting categories from position={}, size={}", from, size);
        Page<CompilationDto> compilations = service.getCompilations(pinned, from, size);
        return new ResponseEntity<>(compilations.getContent(), HttpStatus.OK);
    }
}