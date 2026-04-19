package com.apartmanagebackend.dto.user;

import com.apartmanagebackend.domain.enums.RolUsuario;

public record UsuarioGlobalResponse(
        Long id,
        String email,
        String nombre,
        RolUsuario rol,
        boolean bloqueado
) {
}
