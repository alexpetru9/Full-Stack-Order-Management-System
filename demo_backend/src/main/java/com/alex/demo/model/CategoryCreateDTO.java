package com.alex.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CategoryCreateDTO {

    @NotBlank(message = "Category name cannot be empty!")
    private String name;

    // List of product IDs (for the M:M relationship)
    private List<UUID> productIds;
}