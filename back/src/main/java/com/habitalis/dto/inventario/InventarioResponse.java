package com.habitalis.dto.inventario;

import com.habitalis.domain.enums.CategoriaItem;
import com.habitalis.domain.enums.EstadoItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InventarioResponse(
        Long id,
        String nombre,
        CategoriaItem categoria,
        EstadoItem estado,
        BigDecimal precioCompra,
        LocalDate fechaCompra
) {
}
