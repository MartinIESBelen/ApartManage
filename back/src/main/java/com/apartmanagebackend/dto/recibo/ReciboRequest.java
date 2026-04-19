package com.apartmanagebackend.dto.recibo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReciboRequest(
        @NotNull Integer mes,
        @NotNull Integer anio,
        @NotNull @Positive(message = "El alquiler debe ser mayor a 0") BigDecimal montoAlquiler,
        @NotNull @Positive(message = "La luz debe ser mayor a 0") BigDecimal montoLuz,
        @NotNull @Positive(message = "El agua debe ser mayor a 0") BigDecimal montoAgua
) {}