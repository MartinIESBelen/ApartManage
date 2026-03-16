package com.example.apartmanagebackend.dto.incidencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncidenciaRequest(
        @NotNull Long apartamentoId,
        @NotBlank String titulo,
        @NotBlank String descripcion
) {}