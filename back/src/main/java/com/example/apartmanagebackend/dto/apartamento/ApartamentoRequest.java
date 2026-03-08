package com.example.apartmanagebackend.dto.apartamento;

import jakarta.validation.constraints.NotBlank;

public record ApartamentoRequest (
    @NotBlank String nombre,
    @NotBlank String direccion,
    @NotBlank String ciudad,
    String descripcion
){}
