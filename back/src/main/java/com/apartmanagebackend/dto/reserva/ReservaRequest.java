package com.apartmanagebackend.dto.reserva;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaRequest(
        @NotNull
        @FutureOrPresent(message = "La fecha de entrada no puede ser en el pasado")
        LocalDate fechaEntrada,

        @NotNull
        LocalDate fechaSalida, // La validación lógica (que sea posterior) la hacemos en el servicio

        @NotNull
        @Positive(message = "El precio base debe ser mayor a 0")
        BigDecimal precioBaseAlquiler
) {}
