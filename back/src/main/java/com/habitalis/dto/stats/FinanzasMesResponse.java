package com.habitalis.dto.stats;

import java.math.BigDecimal;

public record FinanzasMesResponse(
        String mes,
        BigDecimal ingresos,
        BigDecimal gastos
) {}
