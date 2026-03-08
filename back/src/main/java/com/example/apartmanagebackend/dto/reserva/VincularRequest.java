package com.example.apartmanagebackend.dto.reserva;

import jakarta.validation.constraints.NotBlank;

public record VincularRequest(
        @NotBlank String codigoVinculacion
) {
}
