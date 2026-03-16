package com.example.apartmanagebackend.controller;

import com.example.apartmanagebackend.domain.enums.EstadoApartamento;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoRequest;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoResponse;
import com.example.apartmanagebackend.service.ApartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apartamentos")
@RequiredArgsConstructor
public class ApartamentoController {

    private final ApartamentoService apartamentoService;

    //POST: /api/v1/apartamentos - > Crear un nuevo piso
    @PostMapping
    public ResponseEntity<ApartamentoResponse> crearApartamento(
            @RequestBody ApartamentoRequest request,
            Principal principal //Spring inyecta automaticamante aquí el email del usuario logueado usando el JWT
    ){
        ApartamentoResponse response = apartamentoService.crearApartamento(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET: /api/v1/apartamentos -> Devuelve la lista de pisos del propietario logueado
    @GetMapping
    public ResponseEntity<List<ApartamentoResponse>> obtenerMisApartamentos(Principal principal) {
        return ResponseEntity.ok(apartamentoService.obtenerMisApartamentos(principal.getName()));
    }

    // GET: /api/v1/apartamentos/{id} -> Devuelve los detalles de 1 piso
    @GetMapping("/{id}")
    public ResponseEntity<ApartamentoResponse> obtenerApartamentoPorId(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(apartamentoService.obtenerApartamentoPorId(id, principal.getName()));
    }

    // GET: /api/v1/apartamentos/filtrar -> Busca pisos según filtros
    @GetMapping("/filtrar")
    public ResponseEntity<List<ApartamentoResponse>> filtrarMisApartamentos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EstadoApartamento estado,
            @RequestParam(required = false) Boolean conAlertas,
            Principal principal
    ) {
        List<ApartamentoResponse> resultados = apartamentoService.filtrarMisApartamentos(
                principal.getName(), nombre, estado, conAlertas);
        return ResponseEntity.ok(resultados);
    }
}
