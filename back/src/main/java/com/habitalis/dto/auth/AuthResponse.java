package com.habitalis.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        Long expiresIn
) {}