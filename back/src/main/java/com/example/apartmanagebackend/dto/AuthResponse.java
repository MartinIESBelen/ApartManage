package com.example.apartmanagebackend.dto;

public record AuthResponse(
        String token
        /*String refreshToken, // Para renovar sesión
        String tipo,         // "Bearer"
        Long expiresIn       // 3600 segundos*/
) {}