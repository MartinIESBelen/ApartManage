package com.habitalis.controller;

import com.habitalis.service.AlmacenamientoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    @Value("${app.almacenamiento.raiz}")
    private String rutaRaiz;

    private final AlmacenamientoService almacenamiento;

    @GetMapping("/**")
    public ResponseEntity<Resource> servirArchivo(
            HttpServletRequest request,
            Principal principal) throws IOException {

        String subruta = extraerSubruta(request);

        Path rutaArchivo = Paths.get(rutaRaiz).resolve(subruta).normalize();
        if (!rutaArchivo.startsWith(Paths.get(rutaRaiz).normalize())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!almacenamiento.tieneAccesoAArchivo(subruta, principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Resource recurso = new UrlResource(rutaArchivo.toUri());
        if (!recurso.exists() || !recurso.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(rutaArchivo);
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(recurso);
    }

    private String extraerSubruta(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String prefijo = "/api/v1/archivos/";
        return uri.substring(uri.indexOf(prefijo) + prefijo.length());
    }
}