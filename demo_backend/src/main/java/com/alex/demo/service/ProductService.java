package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Category;
import com.alex.demo.model.Product;
import com.alex.demo.model.ProductCreateDTO;
import com.alex.demo.repository.CategoryRepository;
import com.alex.demo.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; //needed for M:M validation

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(ProductCreateDTO productDTO) throws ValidationException {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());

        /// ////////////////////////////////
        /// edge cases - bad updates prevention for M:M
        //if the user provided category IDs, we verify that ALL of them exist in the database
        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            List<Category> existingCategories = categoryRepository.findAllById(productDTO.getCategoryIds());

            //if the number of found categories doesn't match the number of IDs provided, one or more are invalid
            if (existingCategories.size() != productDTO.getCategoryIds().size()) {
                throw new ValidationException("One or more Category IDs are invalid. Cannot create product.");
            }
            product.setCategories(existingCategories);
        }
        /// ////////////////////////////////

        return productRepository.save(product);
    }

    public Product updateProduct(UUID uuid, Product product) throws ValidationException {
        Optional<Product> productOptional = productRepository.findById(uuid);

        if (productOptional.isEmpty()) {
            throw new ValidationException("Product with id " + uuid + " not found");
        }
        Product existingProduct = productOptional.get();

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());

        if (product.getCategories() != null) {
            existingProduct.setCategories(product.getCategories());
        }

        return productRepository.save(existingProduct);
    }

    public Product updateProduct2(UUID uuid, Product product) throws ValidationException {
        return productRepository
                .findById(uuid)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    if (product.getCategories() != null) {
                        existingProduct.setCategories(product.getCategories());
                    }
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(
                        () -> new ValidationException("Product with id " + uuid + " not found")
                );
    }

    public void deleteProduct(UUID uuid) {
        productRepository.deleteById(uuid);
    }

    public Product getProductById(UUID uuid) {
        return productRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Product with id " + uuid + " not found"));
    }

    /// ///////////////////
    public Product patchProduct(UUID uuid, Product productUpdates) throws ValidationException {
        Product existingProduct = productRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Product with id " + uuid + " not found"));

        if (productUpdates.getName() != null) {
            existingProduct.setName(productUpdates.getName());
        }
        if (productUpdates.getPrice() != null) {
            existingProduct.setPrice(productUpdates.getPrice());
        }
        if (productUpdates.getCategories() != null) {
            existingProduct.setCategories(productUpdates.getCategories());
        }

        return productRepository.save(existingProduct);
    }
    /// /////////////////
}