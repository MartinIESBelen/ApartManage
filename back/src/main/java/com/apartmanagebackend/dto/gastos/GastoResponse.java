package com.apartmanagebackend.dto.gastos;

import com.apartmanagebackend.domain.enums.CategoriaGasto;
import com.apartmanagebackend.domain.enums.TipoGasto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GastoResponse(
        Long id,
        String concepto,
        CategoriaGasto categoria,
        TipoGasto tipoGasto,
        BigDecimal importe,
        LocalDate fechaGasto,
        Long apartamentoId
) {}
