package ru.practicum.evm.controllers.publiccontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.evm.category.CategoryDto;
import ru.practicum.evm.category.CategoryMapper;
import ru.practicum.evm.category.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/categories")
    public ResponseEntity<Collection<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("GET-request was received at '/categories?from={}&size={}' . Get all categories.", from, size);
        return new ResponseEntity<>(categoryService.getAll(pageable).stream()
                                                   .map(categoryMapper::toCategoryDto)
                                                   .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable @Positive int categoryId) {
        log.info("GET-request was received at '/categories/{}' . Get category by category ID = {}.", categoryId, categoryId);
        return new ResponseEntity<>(categoryService.getById(categoryId), HttpStatus.OK);
    }
}
