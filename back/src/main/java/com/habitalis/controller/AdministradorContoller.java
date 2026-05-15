package com.habitalis.controller;

import com.habitalis.dto.usuario.UsuarioGlobalResponse;
import com.habitalis.service.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdministradorContoller {

    private final AdministradorService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioGlobalResponse>> listarUsuariosGlobal() {
        return ResponseEntity.ok(adminService.listarTodosLosUsuarios());
    }

    @PutMapping("/usuarios/{id}/bloqueo")
    public ResponseEntity<UsuarioGlobalResponse> alternarBloqueoUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.cambiarEstadoBloqueo(id));
    }

}
