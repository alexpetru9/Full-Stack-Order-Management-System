package com.alex.demo.controller;

import com.alex.demo.model.LoginRequest;
import com.alex.demo.model.LoginResponse;
import com.alex.demo.model.RegisterRequest;
import com.alex.demo.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class LoginController {
    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = securityService.login(loginRequest.email(), loginRequest.password());
        if(loginResponse.success()) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(UNAUTHORIZED).body(loginResponse);
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            securityService.registerCustomer(request);
            return ResponseEntity.ok().body(Map.of("success", true, "message", "Account created successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}