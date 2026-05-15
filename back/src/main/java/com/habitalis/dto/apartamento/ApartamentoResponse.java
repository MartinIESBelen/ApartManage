package com.habitalis.dto.apartamento;

import com.habitalis.domain.enums.EstadoApartamento;
import com.habitalis.domain.enums.RelacionVivienda;
import com.habitalis.domain.enums.TipoAlerta;

import java.time.LocalDateTime;
import java.util.Set;

public record ApartamentoResponse(
        Long id,
        String nombreInterno,
        String direccion,
        String ciudad,
        String descripcion,
        EstadoApartamento estado,
        LocalDateTime creadoEn,
        Set<TipoAlerta> alertas,
        RelacionVivienda relacion,
        String inquilinoActual,
        Long contratoActivoId,
        String imagenPrincipal
) {}
