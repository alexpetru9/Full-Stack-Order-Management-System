package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Category;
import com.alex.demo.model.CategoryCreateDTO;
import com.alex.demo.model.Product;
import com.alex.demo.repository.CategoryRepository;
import com.alex.demo.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository; // Needed for M:M validation

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category addCategory(CategoryCreateDTO categoryDTO) throws ValidationException {
        Category category = new Category();
        category.setName(categoryDTO.getName());

        /// ////////////////////////////////
        /// edge cases - bad updates prevention for M:M
        // We verify that the provided Product IDs actually exist in the database
        if (categoryDTO.getProductIds() != null && !categoryDTO.getProductIds().isEmpty()) {
            List<Product> existingProducts = productRepository.findAllById(categoryDTO.getProductIds());

            if (existingProducts.size() != categoryDTO.getProductIds().size()) {
                throw new ValidationException("One or more Product IDs are invalid. Cannot create category.");
            }
            category.setProducts(existingProducts);
        }
        /// ////////////////////////////////

        return categoryRepository.save(category);
    }

    public Category updateCategory(UUID uuid, Category category) throws ValidationException {
        Optional<Category> categoryOptional = categoryRepository.findById(uuid);

        if (categoryOptional.isEmpty()) {
            throw new ValidationException("Category with id " + uuid + " not found");
        }
        Category existingCategory = categoryOptional.get();

        existingCategory.setName(category.getName());

        if (category.getProducts() != null) {
            existingCategory.setProducts(category.getProducts());
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(UUID uuid) {
        categoryRepository.deleteById(uuid);
    }

    public Category getCategoryById(UUID uuid) {
        return categoryRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Category with id " + uuid + " not found"));
    }

    /// ///////////////////
    public Category patchCategory(UUID uuid, Category categoryUpdates) throws ValidationException {
        Category existingCategory = categoryRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Category with id " + uuid + " not found"));

        if (categoryUpdates.getName() != null) {
            existingCategory.setName(categoryUpdates.getName());
        }
        if (categoryUpdates.getProducts() != null) {
            existingCategory.setProducts(categoryUpdates.getProducts());
        }

        return categoryRepository.save(existingCategory);
    }
    /// /////////////////
}