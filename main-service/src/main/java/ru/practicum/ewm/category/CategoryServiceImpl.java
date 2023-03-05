package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category newCategory;
        if (categoryRepository.existsCategoryByName(newCategoryDto.getName())) {
            throw new ConflictException(String.format("Category name = '%s' is already exists", newCategoryDto.getName()));
        } else {
            newCategory = categoryMapper.toCategory(newCategoryDto);
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryDto getById(Long categoryId) {
        return categoryMapper.toCategoryDto(findCategory(categoryId));
    }

    @Override
    @Transactional
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto) {
        Category updatedCategory = findCategory(categoryId);

        String updatedName = newCategoryDto.getName();

        if (categoryRepository.existsCategoryByName(updatedName)) {
            throw new ConflictException(String.format("Category name = '%s' is already exists", updatedName));
        } else {
            updatedCategory.setName(updatedName);
        }

        return categoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public void delete(Long categoryId) {
        checkCategoryExists(categoryId);
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(String.format("Category with id = %s is not empty", categoryId));
        } else {
            categoryRepository.deleteById(categoryId);
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
