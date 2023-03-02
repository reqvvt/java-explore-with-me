package ru.practicum.evm.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.event.EventRepository;
import ru.practicum.evm.exception.ConflictException;
import ru.practicum.evm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional
    public CategoryDto getById(int categoryId) {
        return categoryMapper.toCategoryDto(findCategory(categoryId));
    }

    @Override
    @Transactional
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CategoryDto update(int categoryId, NewCategoryDto newCategoryDto) {
        Category updatedCategory = findCategory(categoryId);

        String updatedName = newCategoryDto.getName();
        updatedCategory.setName(updatedName);

        return categoryMapper.toCategoryDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public void delete(int categoryId) {
        if (!eventRepository.findAllByCategory_Id(categoryId).isEmpty()) {
            throw new ConflictException(String.format("Category with id = %s is not empty", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    private Category findCategory(int categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(
                (String.format("Category with id = %s was not found", categoryId))));
    }
}
