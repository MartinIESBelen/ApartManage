package com.habitalis.controller;

import com.habitalis.domain.enums.EstadoApartamento;
import com.habitalis.dto.apartamento.ApartamentoRequest;
import com.habitalis.dto.apartamento.ApartamentoResponse;
import com.habitalis.service.ApartamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/apartamentos")
@RequiredArgsConstructor
public class ApartamentoController {

    private final ApartamentoService apartamentoService;

    @PostMapping
    public ResponseEntity<ApartamentoResponse> crearApartamento(
            @RequestBody ApartamentoRequest request,
            Principal principal
    ){
        ApartamentoResponse response = apartamentoService.crearApartamento(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartamentoResponse> actualizarApartamento(
            @PathVariable Long id,
            @RequestBody ApartamentoRequest request,
            Principal principal
    ) {
        ApartamentoResponse response = apartamentoService.actualizarApartamento(id, request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApartamentoResponse>> obtenerMisApartamentos(Principal principal) {
        return ResponseEntity.ok(apartamentoService.obtenerMisApartamentos(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartamentoResponse> obtenerApartamentoPorId(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(apartamentoService.obtenerApartamento(id, principal.getName()));
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarApartamento(@PathVariable Long id) {
        try {
            apartamentoService.eliminarApartamento(id);

            return ResponseEntity.ok("Vivienda y todos sus archivos asociados eliminados correctamente.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno al intentar eliminar la vivienda.");
        }
    }

    @PostMapping("/{apartamentoId}/imagenes")
    public ResponseEntity<String> subirImagenApartamento(
            @PathVariable Long apartamentoId,
            @RequestParam("file") MultipartFile file) {
        try {
            apartamentoService.subirImagen(apartamentoId, file);
            return ResponseEntity.ok("Imagen subida con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar el archivo.");
        }
    }

    @GetMapping("/{id}/documentos")
    public ResponseEntity<List<com.habitalis.dto.apartamento.DocumentoResponse>> obtenerDocumentos(
            @PathVariable Long id,
            Principal principal
    ) {
        return ResponseEntity.ok(apartamentoService.obtenerDocumentos(id, principal.getName()));
    }

    @PostMapping("/{id}/documentos")
    public ResponseEntity<String> subirDocumento(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        try {
            apartamentoService.subirDocumento(id, file, principal.getName());
            return ResponseEntity.ok("Documento subido correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().body("Error interno al guardar el archivo.");
        }
    }

    @DeleteMapping("/{id}/documentos/{docId}")
    public ResponseEntity<String> borrarDocumento(
            @PathVariable Long id,
            @PathVariable Long docId,
            Principal principal
    ) {
        try {
            apartamentoService.borrarDocumento(id, docId, principal.getName());
            return ResponseEntity.ok("Documento borrado correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().body("Error interno al borrar el archivo.");
        }
    }

}
