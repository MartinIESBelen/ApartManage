package com.example.apartmanagebackend.controller;

import com.example.apartmanagebackend.domain.enums.EstadoIncidencia;
import com.example.apartmanagebackend.dto.incidencia.*;
import com.example.apartmanagebackend.service.IncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    // POST: /api/v1/incidencias -> Crea una nueva incidencia/ticket
    @PostMapping
    public ResponseEntity<IncidenciaResponse> crear(@RequestBody IncidenciaRequest req, Principal p) {
        return ResponseEntity.ok(incidenciaService.reportarIncidencia(req, p.getName()));
    }

    // PUT: /api/v1/incidencias/{id}/estado -> Actualiza el estado de una incidencia existente
    @PutMapping("/{id}/estado")
    public ResponseEntity<IncidenciaResponse> actualizar(
            @PathVariable Long id,
            @RequestParam EstadoIncidencia estado,
            Principal p) {
        return ResponseEntity.ok(incidenciaService.cambiarEstado(id, estado, p.getName()));
    }
}