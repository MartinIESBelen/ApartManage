package com.apartmanagebackend.dto.transaccion;

import com.apartmanagebackend.domain.enums.CategoriaTransaccion;
import com.apartmanagebackend.domain.enums.EstadoTransaccion;
import com.apartmanagebackend.domain.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionRequest(
        Long apartamentoId,
        Long reservaId,
        boolean dividirEntreTodos,
        TipoTransaccion tipo,
        CategoriaTransaccion categoria,
        EstadoTransaccion estado,
        String concepto,
        BigDecimal importe,
        String comentario,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento
) {
}
