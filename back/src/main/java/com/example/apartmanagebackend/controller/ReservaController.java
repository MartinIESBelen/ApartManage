package com.example.apartmanagebackend.controller;

import com.example.apartmanagebackend.dto.reserva.ReservaRequest;
import com.example.apartmanagebackend.dto.reserva.ReservaResponse;
import com.example.apartmanagebackend.dto.reserva.VincularRequest;
import com.example.apartmanagebackend.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // ACCIÓN DEL PROPIETARIO: Crear la reserva para un apartamento
    @PostMapping("/apartamentos/{apartamentoId}/reservas")
    public ResponseEntity<ReservaResponse> crearReserva(
            @PathVariable Long apartamentoId,
            @RequestBody ReservaRequest request,
            Principal principal
    ) {
        ReservaResponse response = reservaService.crearReserva(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ACCIÓN DEL INQUILINO: Usar el código para vincularse
    @PostMapping("/reservas/vincular")
    public ResponseEntity<ReservaResponse> vincularReserva(
            @RequestBody VincularRequest request,
            Principal principal
    ) {
        ReservaResponse response = reservaService.vincularInquilino(request, principal.getName());
        return ResponseEntity.ok(response);
    }
}
