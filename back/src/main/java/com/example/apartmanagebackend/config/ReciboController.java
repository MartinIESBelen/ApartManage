package com.example.apartmanagebackend.config;

import com.example.apartmanagebackend.dto.recibo.ReciboRequest;
import com.example.apartmanagebackend.dto.recibo.ReciboResponse;
import com.example.apartmanagebackend.service.ReciboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReciboController {

    private final ReciboService reciboService;

    // POST: El propietario emite un recibo
    @PostMapping("/reservas/{reservaId}/recibos")
    public ResponseEntity<ReciboResponse> crearRecibo(
            @PathVariable Long reservaId,
            @RequestBody ReciboRequest request,
            Principal principal
    ) {
        ReciboResponse response = reciboService.crearRecibo(reservaId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Ver todos los recibos de una reserva (Lo usan ambos)
    @GetMapping("/reservas/{reservaId}/recibos")
    public ResponseEntity<List<ReciboResponse>> obtenerRecibos(
            @PathVariable Long reservaId,
            Principal principal
    ) {
        return ResponseEntity.ok(reciboService.obtenerRecibosPorReserva(reservaId, principal.getName()));
    }

    // PUT: El inquilino marca el recibo como pagado
    @PutMapping("/recibos/{reciboId}/pagar")
    public ResponseEntity<ReciboResponse> pagarRecibo(
            @PathVariable Long reciboId,
            Principal principal
    ) {
        return ResponseEntity.ok(reciboService.pagarRecibo(reciboId, principal.getName()));
    }
}