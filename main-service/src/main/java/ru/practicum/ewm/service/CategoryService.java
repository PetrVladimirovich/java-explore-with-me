package ru.practicum.ewm.service;

import org.springframework.data.domain.Page;
import ru.practicum.ewm.dto.CategoryDto;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(CategoryDto dto);

    Page<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Integer catId);
}