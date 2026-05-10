package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.auth.*;
import com.apartmanagebackend.service.AuthService;
import com.apartmanagebackend.service.RecuperacionPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RecuperacionPasswordService recuperacionService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<String> recuperarPassword(
            @RequestBody RecuperarPasswordRequest request) {
        recuperacionService.solicitarRecuperacion(request.email());
        return ResponseEntity.ok("Si el email existe, recibirás un enlace en breve.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {
        try {
            recuperacionService.resetearPassword(request.token(), request.nuevaPassword());
            return ResponseEntity.ok("Contraseña actualizada correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}