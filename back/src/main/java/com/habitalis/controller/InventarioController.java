package com.habitalis.controller;

import com.habitalis.dto.inventario.InventarioRequest;
import com.habitalis.dto.inventario.InventarioResponse;
import com.habitalis.service.InventarioService;
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

    @PostMapping
    public ResponseEntity<InventarioResponse> agregarItem(
            @PathVariable Long apartamentoId,
            @RequestBody InventarioRequest request,
            Principal principal
    ) {
        InventarioResponse response = inventarioService.agregarItem(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponse>> obtenerInventario(
            @PathVariable Long apartamentoId,
            Principal principal
    ) {
        return ResponseEntity.ok(inventarioService.listarInventarioPorApartamento(apartamentoId, principal.getName()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable Long apartamentoId,
            @PathVariable Long itemId,
            Principal principal
    ) {
        inventarioService.eliminarItem(apartamentoId, itemId, principal.getName());
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content (es el estándar para Delete)
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<InventarioResponse> editarItem(
            @PathVariable Long apartamentoId,
            @PathVariable Long itemId,
            @RequestBody InventarioRequest request,
            Principal principal
    ) {
        InventarioResponse response = inventarioService.editarItem(apartamentoId, itemId, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{itemId}/roto")
    public ResponseEntity<InventarioResponse> marcarComoRoto(
            @PathVariable Long apartamentoId,
            @PathVariable Long itemId,
            Principal principal
    ) {
        InventarioResponse response = inventarioService.marcarComoRoto(apartamentoId, itemId, principal.getName());
        return ResponseEntity.ok(response);
    }

}
