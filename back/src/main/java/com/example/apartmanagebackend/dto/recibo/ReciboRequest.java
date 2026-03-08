package com.example.apartmanagebackend.dto.recibo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReciboRequest(
        @NotNull Integer mes,
        @NotNull Integer anio,
        @NotNull BigDecimal montoAlquiler,
        @NotNull BigDecimal montoLuz,
        @NotNull BigDecimal montoAgua
) {}