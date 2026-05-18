package com.alex.demo.model;

import java.util.UUID;

public record LoginResponse(
        Boolean success,
        String role,
        String errorMessage,
        String token,
        UUID id
) {
    public LoginResponse(String errorMessage){
        this(false, null, errorMessage, null, null);
    }

    public LoginResponse(String role, String token, UUID id){
        this(true, role, null, token, id);
    }
}