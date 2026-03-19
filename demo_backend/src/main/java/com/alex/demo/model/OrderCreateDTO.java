package com.alex.demo.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class OrderCreateDTO {

    @NotNull(message = "Order date is required!")
    private LocalDate orderDate;

    @NotNull(message = "The order must be linked to a user (Person ID is required)!")
    private UUID personId;
}