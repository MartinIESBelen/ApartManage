package com.habitalis.dto.transaccion;

import com.habitalis.domain.enums.CategoriaTransaccion;
import com.habitalis.domain.enums.EstadoTransaccion;
import com.habitalis.domain.enums.TipoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionResponse(
        Long id,
        Long apartamentoId,
        String apartamentoNombre,
        Long contratoId,
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