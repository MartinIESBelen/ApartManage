package com.habitalis.dto.contrato;

import com.habitalis.domain.enums.EstadoContrato;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoResponse(
        Long id,
        String codigoVinculacion,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        BigDecimal precioBaseAlquiler,
        EstadoContrato estado,
        String nombreApartamento,
        String nombreInquilino
) {
}
