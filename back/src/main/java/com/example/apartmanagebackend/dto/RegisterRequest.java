package com.example.apartmanagebackend.dto;

import com.example.apartmanagebackend.domain.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombreCompleto,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol
) {
}
