package com.apartmanagebackend.dto.auth;

public record AuthResponse(
        String token,
        String refreshToken,
        String tipo,
        Long expiresIn
) {}