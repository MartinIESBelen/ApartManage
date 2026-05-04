package com.apartmanagebackend.dto.reserva;

import com.apartmanagebackend.domain.enums.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContratoDetalleResponse(
        Long id,
        String codigoVinculacion,
        String nombreApartamento,
        LocalDate fechaEntrada,
        LocalDate fechaFin,
        BigDecimal precioBase,
        BigDecimal fianza,
        EstadoReserva estado,
        LocalDateTime creadoEn,
        InquilinoPublico inquilino
) {
    public record InquilinoPublico(
            Long id,
            String nombre,
            String apellidos,
            String email,
            String telefono
    ) {}
}