package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(CategoryDto dto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Integer catId);
}