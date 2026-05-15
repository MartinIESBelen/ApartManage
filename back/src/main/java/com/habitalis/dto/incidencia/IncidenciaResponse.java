package com.habitalis.dto.incidencia;

import com.habitalis.domain.enums.EstadoIncidencia;
import java.time.LocalDateTime;

public record IncidenciaResponse(
        Long id,
        String titulo,
        String descripcion,
        EstadoIncidencia estado,
        LocalDateTime fechaReporte,
        String nombreApartamento
) {}