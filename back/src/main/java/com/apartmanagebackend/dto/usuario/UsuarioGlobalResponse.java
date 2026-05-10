package com.apartmanagebackend.dto.usuario;

import com.apartmanagebackend.domain.enums.RolUsuario;

public record UsuarioGlobalResponse(
        Long id,
        String email,
        String nombre,
        String apellidos,
        RolUsuario rol,
        boolean bloqueado
) {
}
