package com.alex.demo.model;

import com.alex.demo.validator.StrongPassword;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PersonCreateDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message =
            "Name should be between 2 and 100 characters")
    @Pattern(regexp = "^[^0-9]+$", message = "Name must not contain numbers")
    private String name;

    @NotBlank(message = "Password is required")
    @StrongPassword(message =
            "Password must contain at least 8 characters, including uppercase, " +
                    "lowercase, digit, and special character")
    private String password;


    @NotNull(message = "Age is required")
    private Integer age;

    @NotBlank(message = "Email is required")
    @Email
    private String email;
}
