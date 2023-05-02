package ru.practicum.ewm.controller.pub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@AllArgsConstructor
@Controller
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService service;

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable("compId") Long compId) {
        log.info("Getting a selection id={}", compId);
        return ResponseEntity.ok(service.getCompilation(compId));
    }

    @GetMapping()
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                @Positive  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting categories from position={}, size={}", from, size);
        return ResponseEntity.ok(service.getCompilations(pinned, from, size));
    }
}