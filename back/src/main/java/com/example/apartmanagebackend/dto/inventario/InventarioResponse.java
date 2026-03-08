package com.example.apartmanagebackend.dto.inventario;

import com.example.apartmanagebackend.domain.enums.CategoriaItem;
import com.example.apartmanagebackend.domain.enums.EstadoItem;

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
