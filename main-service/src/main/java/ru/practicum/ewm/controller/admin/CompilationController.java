package ru.practicum.ewm.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/compilations")
public class CompilationController {
    private final CompilationService service;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @NotNull @Valid NewCompilationDto dto) {
        log.info("Adding a new collection: {}", dto);
        return new ResponseEntity<>(service.createCompilation(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@RequestBody @NotNull @Valid UpdateCompilationDto dto,
                                                            @PathVariable(name = "compId") Long compId) {
        log.info("Updating the collection id={}: {}", compId, dto);
        return new ResponseEntity<>(service.updateCompilation(compId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Deleting collection id={}", compId);
        service.deleteCompilation(compId);
    }
}