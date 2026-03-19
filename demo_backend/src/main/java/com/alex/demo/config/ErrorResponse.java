package com.alex.demo.config;

public record ErrorResponse(
        String message,
        String details,
        String path,
        String timestamp
) {
}
