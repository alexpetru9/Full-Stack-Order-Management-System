package com.alex.demo.controller;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Category;
import com.alex.demo.model.CategoryCreateDTO;
import com.alex.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/category/{uuid}")
    public Category getCategoryById(@PathVariable UUID uuid) {
        return categoryService.getCategoryById(uuid);
    }

    @PostMapping("/category")
    public Category addCategory(
            @Valid @RequestBody CategoryCreateDTO categoryDTO
    ) throws ValidationException {
        return categoryService.addCategory(categoryDTO);
    }

    @PutMapping("/category/{uuid}")
    public Category updateCategory(@PathVariable UUID uuid,
                                   @RequestBody Category category)
            throws ValidationException {
        return categoryService.updateCategory(uuid, category);
    }

    @DeleteMapping("/category/{uuid}")
    public void deleteCategory(@PathVariable UUID uuid) {
        categoryService.deleteCategory(uuid);
    }

    /// ///////////////////////////
    @PatchMapping("/category/{uuid}")
    public Category patchCategory(@PathVariable UUID uuid, @RequestBody Category categoryUpdates) throws ValidationException {
        return categoryService.patchCategory(uuid, categoryUpdates);
    }
}