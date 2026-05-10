package com.apartmanagebackend.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        @NotBlank(message = "El refresh accessToken es obligatorio")
        String refreshToken

) {}
