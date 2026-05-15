package com.habitalis.controller;

import com.habitalis.dto.usuario.UsuarioPerfilResponse;
import com.habitalis.dto.usuario.UsuarioUpdateRequest;
import com.habitalis.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;


    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> obtenerMiPerfil(Principal principal) {
        return ResponseEntity.ok(usuarioService.obtenerMiPerfil(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> actualizarMiPerfil(
            Principal principal,
            @RequestBody UsuarioUpdateRequest request) {

        return ResponseEntity.ok(usuarioService.actualizarMiPerfil(principal.getName(), request));
    }

    @PostMapping("/{usuarioId}/imagen")
    public ResponseEntity<String> subirImagenPerfil(
            @PathVariable Long usuarioId,
            @RequestParam("file") MultipartFile file) {

        try {
            usuarioService.subirImagenPerfil(usuarioId, file);
            return ResponseEntity.ok("Imagen de perfil subida y actualizada con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al procesar la imagen de perfil: " + e.getMessage());
        }
    }

}