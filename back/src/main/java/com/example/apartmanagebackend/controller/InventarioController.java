package com.example.apartmanagebackend.controller;

import com.example.apartmanagebackend.dto.inventario.InventarioRequest;
import com.example.apartmanagebackend.dto.inventario.InventarioResponse;
import com.example.apartmanagebackend.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apartamentos/{apartamentoId}/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    // POST: Crear un nuevo mueble en un apartamento específico
    @PostMapping
    public ResponseEntity<InventarioResponse> agregarItem(
            @PathVariable Long apartamentoId,
            @RequestBody InventarioRequest request,
            Principal principal
    ) {
        InventarioResponse response = inventarioService.agregarItem(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Ver todos los muebles de un apartamento específico
    @GetMapping
    public ResponseEntity<List<InventarioResponse>> obtenerInventario(
            @PathVariable Long apartamentoId,
            Principal principal
    ) {
        return ResponseEntity.ok(inventarioService.listarInventarioPorApartamento(apartamentoId, principal.getName()));
    }

}
