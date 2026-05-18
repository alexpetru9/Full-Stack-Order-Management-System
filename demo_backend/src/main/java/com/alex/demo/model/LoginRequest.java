package com.alex.demo.model;

public record LoginRequest(
        String email,
        String password
) {
}