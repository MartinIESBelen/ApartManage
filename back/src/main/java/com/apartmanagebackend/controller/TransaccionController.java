package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.transaccion.TransaccionRequest;
import com.apartmanagebackend.dto.transaccion.TransaccionResponse;
import com.apartmanagebackend.service.TransaccionService;
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

    /**
     * GET: Obtener transacciones filtradas por periodo y (opcionalmente) apartamento.
     *
     */
    @GetMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesFiltradas(
            @RequestParam(required = false) Long apartamentoId,
            @RequestParam String periodo,
            Principal principal) {

        // principal.getName() nos da el email del propietario logueado para que no vea pisos de otros.
        List<TransaccionResponse> transacciones = transaccionService.obtenerFiltradas(apartamentoId, periodo, principal.getName());

        return ResponseEntity.ok(transacciones);
    }

    /**
     * POST: Crear una nueva transacción (Ingreso o Gasto).
     * Si en el Request viene "dividirEntreTodos = true", devolverá una lista con las transacciones generadas.
     */
    @PostMapping
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<List<TransaccionResponse>> crearTransaccion(
            @RequestBody TransaccionRequest request,
            Principal principal) {

        List<TransaccionResponse> nuevasTransacciones = transaccionService.crearTransaccion(request, principal.getName());
        return new ResponseEntity<>(nuevasTransacciones, HttpStatus.CREATED);
    }

    /**
     * GET: Obtener todas las transacciones de un apartamento.
     * Útil para la pestaña de "Finanzas" de un piso concreto.
     */
    @GetMapping("/apartamento/{apartamentoId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'ADMIN')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesPorApartamento(
            @PathVariable Long apartamentoId) {

        List<TransaccionResponse> transacciones = transaccionService.obtenerPorApartamento(apartamentoId);
        return ResponseEntity.ok(transacciones);
    }

    /**
     * GET: Obtener las transacciones de una reserva en concreto.
     * Útil para que el Inquilino vea sus propios recibos.
     */
    @GetMapping("/reserva/{reservaId}")
    @PreAuthorize("hasAnyRole('PROPIETARIO', 'INQUILINO', 'ADMIN')")
    public ResponseEntity<List<TransaccionResponse>> obtenerTransaccionesPorReserva(
            @PathVariable Long reservaId) {

        List<TransaccionResponse> transacciones = transaccionService.obtenerPorReserva(reservaId);
        return ResponseEntity.ok(transacciones);
    }
}