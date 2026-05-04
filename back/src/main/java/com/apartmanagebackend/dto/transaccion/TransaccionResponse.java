package com.apartmanagebackend.dto.transaccion;

import com.apartmanagebackend.domain.enums.CategoriaTransaccion;
import com.apartmanagebackend.domain.enums.EstadoTransaccion;
import com.apartmanagebackend.domain.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionResponse(
        Long id,
        Long apartamentoId,
        String apartamentoNombre,
        Long reservaId,
        String inquilinoNombre,
        TipoTransaccion tipo,
        CategoriaTransaccion categoria,
        EstadoTransaccion estado,
        String concepto,
        BigDecimal importe,
        String comentario,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        LocalDate fechaPago
) {}