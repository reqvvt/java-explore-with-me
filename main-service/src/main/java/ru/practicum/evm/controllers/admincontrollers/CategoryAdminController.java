package ru.practicum.evm.controllers.admincontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.evm.category.CategoryDto;
import ru.practicum.evm.category.CategoryService;
import ru.practicum.evm.category.NewCategoryDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST-request was received at 'admin/categories'. Create a CATEGORY: {}.", newCategoryDto);
        return new ResponseEntity<>(categoryService.create(newCategoryDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> patchCategoryById(@PathVariable int categoryId,
                                                         @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("PATCH-request was received at 'admin/categories/{}'. Patch a CATEGORY with categoryID = {}. " +
                "New data = {}", categoryId, categoryId, newCategoryDto);
        return new ResponseEntity<>(categoryService.update(categoryId, newCategoryDto), HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable int categoryId) {
        log.info("DELETE-request was received at 'admin/categories/{}'. Delete a CATEGORY with categoryID = {}.",
                categoryId, categoryId);
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

