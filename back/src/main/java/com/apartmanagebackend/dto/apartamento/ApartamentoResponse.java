package com.apartmanagebackend.dto.apartamento;

import com.apartmanagebackend.domain.enums.EstadoApartamento;
import com.apartmanagebackend.domain.enums.RelacionVivienda;

import java.time.LocalDateTime;
import java.util.List;

public record ApartamentoResponse(
        Long id,
        String nombreInterno,
        String direccion,
        String ciudad,
        String descripcion,
        EstadoApartamento estado,
        LocalDateTime creadoEn,
        List<String> alertas,
        RelacionVivienda relacion,
        String inquilinoActual,
        Long contratoActivoId,
        String imagenPrincipal
) {}
