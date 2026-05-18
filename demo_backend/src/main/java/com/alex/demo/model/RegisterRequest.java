package com.alex.demo.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private Integer age;
    private String password;
}