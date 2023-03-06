package ru.practicum.ewm.apiPublic.controller;

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
import ru.practicum.ewm.apiPublic.service.CategoryPublicService;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.CategoryMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryPublicService service;
    private final CategoryMapper categoryMapper;

    @GetMapping("/categories")
    public ResponseEntity<Collection<CategoryDto>> getAll(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("GET-request was received at '/categories?from={}&size={}' . Get all categories.", from, size);
        return new ResponseEntity<>(service.getAll(pageable).stream()
                                           .map(categoryMapper::toCategoryDto)
                                           .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable @Positive Long categoryId) {
        log.info("GET-request was received at '/categories/{}' . Get category by category ID = {}.", categoryId, categoryId);
        return new ResponseEntity<>(service.getById(categoryId), HttpStatus.OK);
    }
}
