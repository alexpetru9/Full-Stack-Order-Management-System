package com.alex.demo.controller;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Product;
import com.alex.demo.model.ProductCreateDTO;
import com.alex.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/product/{uuid}")
    public Product getProductById(@PathVariable UUID uuid) {
        return productService.getProductById(uuid);
    }

    @PostMapping("/product")
    public Product addProduct(
            @Valid @RequestBody ProductCreateDTO productDTO
    ) throws ValidationException {
        return productService.addProduct(productDTO);
    }

    @PutMapping("/product/{uuid}")
    public Product updateProduct(@PathVariable UUID uuid,
                                 @RequestBody Product product)
            throws ValidationException {
        return productService.updateProduct(uuid, product);
    }

    @DeleteMapping("/product/{uuid}")
    public void deleteProduct(@PathVariable UUID uuid) {
        productService.deleteProduct(uuid);
    }

    /// ///////////////////////////
    @PatchMapping("/product/{uuid}")
    public Product patchProduct(@PathVariable UUID uuid, @RequestBody Product productUpdates) throws ValidationException {
        return productService.patchProduct(uuid, productUpdates);
    }
}