package com.example.apartmanagebackend.dto.stats;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        long totalApartamentos,
        long apartamentosOcupados,
        BigDecimal ingresosMesActual,
        BigDecimal deudaPendienteTotal,
        long incidenciasAbiertas
) {}