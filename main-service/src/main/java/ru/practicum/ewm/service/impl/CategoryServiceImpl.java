package ru.practicum.ewm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        dto.setName(dto.getName().trim());
        return mapper.toCategoryDto(repository.save(mapper.toCategory(dto)));
    }

    @Override
    public void deleteCategory(Integer catId) {
        try {
            repository.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Category with id=" + catId + " was not found");
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto dto) {
        repository.findById(dto.getId())
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id=%d was not found", dto.getId())));
        return mapper.toCategoryDto(repository.save(mapper.toCategory(dto)));
    }

    @Override
    public Page<CategoryDto> getCategories(Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<Category> categoriesPage = repository.findAll(page);
        List<CategoryDto> categoryDtos = mapper.toCategoryDtos(categoriesPage.getContent());
        return new PageImpl<>(categoryDtos.stream()
                .sorted(Comparator.comparing(CategoryDto::getId))
                .collect(Collectors.toList()), categoriesPage.getPageable(), categoriesPage.getTotalElements());
    }

    @Override
    public CategoryDto getCategory(Integer catId) {
        return mapper.toCategoryDto(repository.findById(catId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id=%d was not found", catId))));
    }
}