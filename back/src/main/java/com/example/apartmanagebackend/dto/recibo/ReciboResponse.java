package com.example.apartmanagebackend.dto.recibo;

import com.example.apartmanagebackend.domain.enums.EstadoRecibo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReciboResponse(
        Long id,
        Integer mes,
        Integer anio,
        BigDecimal montoAlquiler,
        BigDecimal montoLuz,
        BigDecimal montoAgua,
        BigDecimal totalPagar,
        EstadoRecibo estado,
        LocalDate fechaPago,
        Long reservaId
) {
}
