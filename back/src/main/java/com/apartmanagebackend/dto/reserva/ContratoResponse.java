package com.apartmanagebackend.dto.reserva;

import com.apartmanagebackend.domain.enums.EstadoReserva;

public record ContratoResponse(
        Long id,
        String codigoVinculacion,
        String nombreApartamento,
        String nombreInquilino,
        EstadoReserva estado
) {
}
