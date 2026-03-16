package com.example.apartmanagebackend.controller;

import com.example.apartmanagebackend.dto.auth.AuthResponse;
import com.example.apartmanagebackend.dto.auth.LoginRequest;
import com.example.apartmanagebackend.dto.auth.RegisterRequest;
import com.example.apartmanagebackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST: /api/v1/auth/register -> Crea un nuevo registro de usuario en el sistema
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST: /api/v1/auth/login -> Autentica a un usuario existente y permite el inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}