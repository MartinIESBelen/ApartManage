package com.habitalis.dto.usuario;

import java.time.LocalDate;

public record UsuarioUpdateRequest(
        String nombre,
        String apellidos,
        String telefono,
        String dniPasaporte,
        LocalDate fechaNacimiento
) {
}