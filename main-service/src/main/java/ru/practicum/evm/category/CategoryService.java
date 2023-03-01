package ru.practicum.evm.category;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto getById(int categoryId);

    Collection<CategoryDto> getAll(int from, int size);

    CategoryDto update(int categoryId, NewCategoryDto newCategoryDto);

    void delete(int categoryId);
}
