package com.habitalis.controller;

import com.habitalis.domain.enums.EstadoIncidencia;
import com.habitalis.dto.incidencia.IncidenciaRequest;
import com.habitalis.dto.incidencia.IncidenciaResponse;
import com.habitalis.service.IncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @PostMapping
    public ResponseEntity<IncidenciaResponse> crear(@RequestBody IncidenciaRequest req, Principal p) {
        return ResponseEntity.ok(incidenciaService.reportarIncidencia(req, p.getName()));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<IncidenciaResponse> actualizar(
            @PathVariable Long id,
            @RequestParam EstadoIncidencia estado,
            Principal p) {
        return ResponseEntity.ok(incidenciaService.cambiarEstado(id, estado, p.getName()));
    }
}