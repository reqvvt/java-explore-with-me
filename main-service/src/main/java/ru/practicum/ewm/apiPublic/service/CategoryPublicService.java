package ru.practicum.ewm.apiPublic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.NewCategoryDto;

public interface CategoryPublicService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto getById(Long categoryId);

    Page<Category> getAll(Pageable pageable);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto);

    void delete(Long categoryId);
}
