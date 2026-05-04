package com.apartmanagebackend.dto.stats;

import java.math.BigDecimal;

public record FinanzasMesResponse(
        String mes,
        BigDecimal ingresos,
        BigDecimal gastos
) {}
