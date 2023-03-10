package ru.practicum.ewm.apiAdmin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.apiAdmin.service.CategoryAdminService;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.category.NewCategoryDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryAdminService service;

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST-request was received at 'admin/categories'. Create a CATEGORY: {}.", newCategoryDto);
        return new ResponseEntity<>(service.save(newCategoryDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long categoryId,
                                              @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("PATCH-request was received at 'admin/categories/{}'. Patch a CATEGORY with categoryID = {}. " +
                "New data = {}", categoryId, categoryId, newCategoryDto);
        return new ResponseEntity<>(service.update(categoryId, newCategoryDto), HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        log.info("DELETE-request was received at 'admin/categories/{}'. Delete a CATEGORY with categoryID = {}.",
                categoryId, categoryId);
        service.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

