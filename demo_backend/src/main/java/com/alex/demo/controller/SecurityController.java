package com.alex.demo.controller;

import com.alex.demo.model.RegisterRequest;
import com.alex.demo.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            securityService.processForgotPassword(email);
            return ResponseEntity.ok().body(Map.of("message", "Reset code sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            securityService.resetPassword(email, token, newPassword);
            return ResponseEntity.ok().body(Map.of("message", "Password successfully updated."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            securityService.registerCustomer(request);
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Account created successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}