package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.reserva.*;
import com.apartmanagebackend.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // POST: Crear la reserva para un apartamento
    @PostMapping("/apartamentos/{apartamentoId}")
    public ResponseEntity<ReservaResponse> crearReserva(
            @PathVariable Long apartamentoId,
            @RequestBody ReservaRequest request,
            Principal principal
    ) {
        ReservaResponse response = reservaService.crearReserva(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST: Crear reserva con inquilino manual
    @PostMapping("/apartamentos/{apartamentoId}/manual")
    public ResponseEntity<ReservaResponse> crearReservaManual(
            @PathVariable Long apartamentoId,
            @RequestBody ReservaManualRequest request,
            Principal principal
    ) {
        ReservaResponse response = reservaService.crearReservaManual(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Ver las reservas/contratos de un piso concreto
    @GetMapping("/apartamentos/{apartamentoId}")
    public ResponseEntity<List<ReservaResponse>> listarReservas(
            @PathVariable Long apartamentoId,
            Principal principal
    ) {
        return ResponseEntity.ok(reservaService.listarReservasPorApartamento(apartamentoId, principal.getName()));
    }

    // GET: Ver TODAS las reservas del propietario (Para la lista principal de Angular)
    @GetMapping
    @PreAuthorize("hasAuthority('PROPIETARIO')")
    public ResponseEntity<List<ContratoResponse>> obtenerMisContratos(Principal principal) {
        return ResponseEntity.ok(reservaService.obtenerMisContratosPropietario(principal.getName()));
    }

    // GET: Ver el detalle de 1 solo contrato
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROPIETARIO')")
    public ResponseEntity<ContratoDetalleResponse> obtenerDetalleContrato(
            @PathVariable Long id,
            Principal principal) {
        ContratoDetalleResponse detalle = reservaService.obtenerDetalleContrato(id, principal.getName());
        return ResponseEntity.ok(detalle);
    }

    // POST: Vincular un inquilino con un código
    @PostMapping("/vincular")
    public ResponseEntity<ReservaResponse> vincularReserva(
            @RequestBody VincularRequest request,
            Principal principal
    ) {
        ReservaResponse response = reservaService.vincularInquilino(request, principal.getName());
        return ResponseEntity.ok(response);
    }
}
