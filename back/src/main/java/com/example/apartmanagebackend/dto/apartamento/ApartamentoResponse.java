package com.example.apartmanagebackend.dto.apartamento;

import com.example.apartmanagebackend.domain.enums.EstadoApartamento;

import java.time.LocalDateTime;

public record ApartamentoResponse(
        Long id,
        String nombreInterno,
        String direccion,
        String ciudad,
        String descripcion,
        EstadoApartamento estado,
        LocalDateTime creadoEn
) {}
