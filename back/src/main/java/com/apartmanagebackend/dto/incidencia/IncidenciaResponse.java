package com.apartmanagebackend.dto.incidencia;

import com.apartmanagebackend.domain.enums.EstadoIncidencia;
import java.time.LocalDateTime;

public record IncidenciaResponse(
        Long id,
        String titulo,
        String descripcion,
        EstadoIncidencia estado,
        LocalDateTime fechaReporte,
        String nombreApartamento
) {}