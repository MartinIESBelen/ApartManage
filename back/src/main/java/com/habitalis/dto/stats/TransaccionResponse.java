package com.habitalis.dto.stats;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionResponse(
        String id,
        LocalDate fecha,
        String concepto,
        String tipo,
        BigDecimal monto,
        String estado
) {
}
