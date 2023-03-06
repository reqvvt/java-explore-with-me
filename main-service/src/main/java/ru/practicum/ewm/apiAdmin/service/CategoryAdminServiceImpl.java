package ru.practicum.ewm.apiAdmin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.*;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.DataValidateException;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto save(NewCategoryDto newCategoryDto) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = findCategory(categoryId);

        if(newCategoryDto.getName() == null) {
            throw new DataValidateException("Field: name. Error: must not be blank. Value: null");
        }

        String updatedName = newCategoryDto.getName();

        if (categoryRepository.existsCategoryByName(updatedName)) {
            throw new ConflictException(String.format("Category name = '%s' is already exists", updatedName));
        } else {
            category.setName(updatedName);
        }

        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void delete(Long categoryId) {
        checkCategoryExists(categoryId);
        if (eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new ConflictException(String.format("Category with id = %s is not empty", categoryId));
        }
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", categoryId))));
    }

    private void checkCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException((String.format("Category with id = %s was not found", categoryId)));
        }
    }
}
