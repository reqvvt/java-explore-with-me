package ru.practicum.evm.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto getById(int categoryId);

    Page<Category> getAll(Pageable pageable);

    CategoryDto update(int categoryId, NewCategoryDto newCategoryDto);

    void delete(int categoryId);
}
