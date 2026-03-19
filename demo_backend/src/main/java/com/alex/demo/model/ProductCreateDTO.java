package com.alex.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductCreateDTO {

    @NotBlank(message = "Product name cannot be empty!")
    private String name;

    @NotNull(message = "Product price is required!")
    private Double price;

    // A list of category IDs to link this product to multiple categories (M:M)
    private List<UUID> categoryIds;
}