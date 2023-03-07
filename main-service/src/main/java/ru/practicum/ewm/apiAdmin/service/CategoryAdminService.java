package ru.practicum.ewm.apiAdmin.service;

import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto save(NewCategoryDto newCategoryDto);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto);

    void delete(Long categoryId);
}
