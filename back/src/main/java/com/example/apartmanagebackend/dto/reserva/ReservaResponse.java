package com.example.apartmanagebackend.dto.reserva;

import com.example.apartmanagebackend.domain.enums.EstadoReserva;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ReservaResponse(
        Long id,
        String codigoVinculacion,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        BigDecimal precioBaseAlquiler,
        EstadoReserva estado,
        String nombreApartamento,
        String nombreInquilino // Puede ser null si aún no se ha vinculado nadie
) {}