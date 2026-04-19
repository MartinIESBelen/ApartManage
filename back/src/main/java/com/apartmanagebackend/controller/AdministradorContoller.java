package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.user.UsuarioGlobalResponse;
import com.apartmanagebackend.service.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdministradorContoller {

    private final AdministradorService adminService;

    // GET: /api/v1/admin/usuarios -> Devuelve la lista global filtrada sin contraseñas
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioGlobalResponse>> listarUsuariosGlobal() {
        return ResponseEntity.ok(adminService.listarTodosLosUsuarios());
    }

    // PUT: /api/v1/admin/usuarios/{id}/bloqueo -> Alterna el estado de baneo de un usuario
    @PutMapping("/usuarios/{id}/bloqueo")
    public ResponseEntity<UsuarioGlobalResponse> alternarBloqueoUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.cambiarEstadoBloqueo(id));
    }

}
