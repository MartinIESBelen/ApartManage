package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.gastos.GastoRequest;
import com.apartmanagebackend.dto.gastos.GastoResponse;
import com.apartmanagebackend.service.GastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apartamentos/{apartamentoId}/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService gastoService;

    // POST: Añadir un nuevo gasto a un piso
    @PostMapping
    public ResponseEntity<GastoResponse> añadirGasto(
            @PathVariable Long apartamentoId,
            @RequestBody GastoRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gastoService.añadirGasto(apartamentoId, request, principal.getName()));
    }

    // GET: Ver todos los gastos de un piso
    @GetMapping
    public ResponseEntity<List<GastoResponse>> obtenerGastos(
            @PathVariable Long apartamentoId,
            Principal principal
    ) {
        return ResponseEntity.ok(gastoService.obtenerGastosPorApartamento(apartamentoId, principal.getName()));
    }

    // DELETE: Borrar un gasto equivocado
    @DeleteMapping("/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(
            @PathVariable Long apartamentoId,
            @PathVariable Long gastoId,
            Principal principal
    ) {
        gastoService.eliminarGasto(gastoId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
