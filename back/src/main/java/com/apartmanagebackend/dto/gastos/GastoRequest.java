package com.apartmanagebackend.dto.gastos;

import com.apartmanagebackend.domain.enums.CategoriaGasto;
import com.apartmanagebackend.domain.enums.TipoGasto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoRequest(
        @NotBlank String concepto,
        @NotNull CategoriaGasto categoria,
        @NotNull TipoGasto tipoGasto,
        @NotNull BigDecimal importe,
        @NotNull LocalDate fechaGasto
) {}
