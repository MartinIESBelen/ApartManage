package com.example.apartmanagebackend.dto.auth;

public record AuthResponse(
        String token
        /* * Estos campos están comentados temporalmente para simplificar el
         * desarrollo inicial y evitar la lógica compleja de expiración/renovación.
         * Se descomentarán posteriormente cuando preparemos la app para producción.
         */
        /*String refreshToken, // Para renovar sesión
        String tipo,// "Bearer"
        Long expiresIn// 3600 segundos*/
) {}