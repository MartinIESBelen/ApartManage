package com.habitalis.dto.usuario;

import com.habitalis.domain.enums.RolUsuario;

public record UsuarioGlobalResponse(
        Long id,
        String email,
        String nombre,
        String apellidos,
        RolUsuario rol,
        boolean bloqueado
) {
}
