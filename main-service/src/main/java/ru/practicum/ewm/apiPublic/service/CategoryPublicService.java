package ru.practicum.ewm.apiPublic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryDto;

public interface CategoryPublicService {

    CategoryDto getById(Long categoryId);

    Page<Category> getAll(Pageable pageable);
}
