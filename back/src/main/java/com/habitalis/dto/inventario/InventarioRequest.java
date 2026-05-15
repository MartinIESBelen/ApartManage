package com.habitalis.dto.inventario;

import com.habitalis.domain.enums.CategoriaItem;
import com.habitalis.domain.enums.EstadoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InventarioRequest(
        @NotBlank String nombre,
        @NotNull CategoriaItem categoria,
        @NotNull EstadoItem estado,
        BigDecimal precioCompra,
        LocalDate fechaCompra
) {
}
