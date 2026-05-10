package com.apartmanagebackend.dto.contrato;

import jakarta.validation.constraints.NotBlank;

public record VincularRequest(
        @NotBlank String codigoVinculacion
) {
}
