package com.habitalis.controller;

import com.habitalis.dto.transaccion.TransaccionRequest;
import com.habitalis.dto.transaccion.TransaccionResponse;
import com.habitalis.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;


    @GetMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesFiltradas(
            @RequestParam(required = false) Long apartamentoId,
            @RequestParam String periodo,
            Principal principal) {

        List<TransaccionResponse> transacciones = transaccionService.obtenerFiltradas(apartamentoId, periodo, principal.getName());

        return ResponseEntity.ok(transacciones);
    }

    @PostMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<TransaccionResponse>> crearTransaccion(
            @RequestBody TransaccionRequest request,
            Principal principal) {

        List<TransaccionResponse> nuevasTransacciones = transaccionService.crearTransaccion(request, principal.getName());
        return new ResponseEntity<>(nuevasTransacciones, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<TransaccionResponse> actualizarTransaccion(
            @PathVariable Long id,
            @RequestBody TransaccionRequest request,
            Principal principal) {

        TransaccionResponse actualizada = transaccionService.actualizarTransaccion(id, request, principal.getName());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<Void> eliminarTransaccion(
            @PathVariable Long id,
            Principal principal) {

        transaccionService.eliminarTransaccion(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/apartamento/{apartamentoId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesPorApartamento(
            @PathVariable Long apartamentoId,
            Principal principal) {

        List<TransaccionResponse> transacciones = transaccionService.obtenerPorApartamento(apartamentoId, principal.getName());
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/contrato/{contratoId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'INQUILINO', 'ADMIN')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesPorContrato(
            @PathVariable Long contratoId,
            Principal principal) {

        List<TransaccionResponse> transacciones = transaccionService.obtenerPorContrato(contratoId, principal.getName());
        return ResponseEntity.ok(transacciones);
    }

}