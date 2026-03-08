package com.example.apartmanagebackend.dto.reserva;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaRequest(
        @NotNull LocalDate fechaEntrada,
        @NotNull LocalDate fechaSalida,
        @NotNull BigDecimal precioBaseAlquiler
) {
}
