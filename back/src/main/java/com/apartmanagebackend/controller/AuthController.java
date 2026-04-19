package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.auth.AuthResponse;
import com.apartmanagebackend.dto.auth.LoginRequest;
import com.apartmanagebackend.dto.auth.RefreshTokenRequest;
import com.apartmanagebackend.dto.auth.RegisterRequest;
import com.apartmanagebackend.service.AuthService;
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

    // POST: /api/v1/auth/refresh -> Renueva el access token usando el refresh token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}