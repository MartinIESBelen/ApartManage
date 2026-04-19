package com.apartmanagebackend.dto.inventario;

import com.apartmanagebackend.domain.enums.CategoriaItem;
import com.apartmanagebackend.domain.enums.EstadoItem;

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
