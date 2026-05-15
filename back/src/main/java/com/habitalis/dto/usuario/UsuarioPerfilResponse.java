package com.habitalis.dto.usuario;

import java.time.LocalDate;

public record UsuarioPerfilResponse(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String dniPasaporte,
        LocalDate fechaNacimiento,
        String rol,
        String imagenPerfil
) {
}