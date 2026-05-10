package com.apartmanagebackend.controller;

import com.apartmanagebackend.dto.contrato.*;
import com.apartmanagebackend.service.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    // POST: Crear la contrato para un apartamento
    @PostMapping("/apartamentos/{apartamentoId}")
    public ResponseEntity<ContratoResponse> crearContrato(
            @PathVariable Long apartamentoId,
            @RequestBody ContratoRequest request,
            Principal principal
    ) {
        ContratoResponse response = contratoService.crearContrato(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST: Crear contrato con inquilino manual
    @PostMapping("/apartamentos/{apartamentoId}/manual")
    public ResponseEntity<ContratoResponse> crearContratoManual(
            @PathVariable Long apartamentoId,
            @RequestBody ContratoManualRequest request,
            Principal principal
    ) {
        ContratoResponse response = contratoService.crearContratoManual(apartamentoId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: Ver las contratos/contratos de un piso concreto
    @GetMapping("/apartamentos/{apartamentoId}")
    public ResponseEntity<List<ContratoResponse>> listarContratos(
            @PathVariable Long apartamentoId,
            Principal principal
    ) {
        return ResponseEntity.ok(contratoService.listarContratosPorApartamento(apartamentoId, principal.getName()));
    }

    // GET: Ver todos los contratos del propietario (Para la lista principal de Angular)
    @GetMapping
    @PreAuthorize("hasAuthority('PROPIETARIO')")
    public ResponseEntity<List<ContratoResponse>> obtenerMisContratos(Principal principal) {
        return ResponseEntity.ok(contratoService.obtenerMisContratosPropietario(principal.getName()));
    }

    // GET: Ver el detalle de 1 solo contrato
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PROPIETARIO')")
    public ResponseEntity<ContratoDetalleResponse> obtenerDetalleContrato(
            @PathVariable Long id,
            Principal principal) {
        ContratoDetalleResponse detalle = contratoService.obtenerDetalleContrato(id, principal.getName());
        return ResponseEntity.ok(detalle);
    }

    // POST: Vincular un inquilino con un código
    @PostMapping("/vincular")
    public ResponseEntity<ContratoResponse> vincularContrato(
            @RequestBody VincularRequest request,
            Principal principal
    ) {
        ContratoResponse response = contratoService.vincularInquilino(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{contratoId}/contrato")
    public ResponseEntity<String> subirContrato(
            @PathVariable Long contratoId,
            @RequestParam("file") MultipartFile file) {
        try {
            contratoService.subirContratoPdf(contratoId, file);
            return ResponseEntity.ok("Contrato PDF subido con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al procesar el documento: " + e.getMessage());
        }
    }
}
