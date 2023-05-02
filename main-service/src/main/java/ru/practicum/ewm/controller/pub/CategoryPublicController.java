package ru.practicum.ewm.controller.pub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PositiveOrZero  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting categories from position={}, size={}", from, size);
        return ResponseEntity.ok(service.getCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable(name = "catId") Integer catId) {
        log.info("Getting categories id={}", catId);
        return ResponseEntity.ok(service.getCategory(catId));
    }
}