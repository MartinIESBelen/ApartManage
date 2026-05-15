package com.habitalis.service;

import com.habitalis.domain.Usuario;
import com.habitalis.dto.usuario.UsuarioPerfilResponse;
import com.habitalis.dto.usuario.UsuarioUpdateRequest;
import com.habitalis.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlmacenamientoService almacenamiento;

    public UsuarioPerfilResponse obtenerMiPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToResponse(usuario);
    }

    @Transactional
    public UsuarioPerfilResponse actualizarMiPerfil(String email, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setTelefono(request.telefono());
        usuario.setDniPasaporte(request.dniPasaporte());
        usuario.setFechaNacimiento(request.fechaNacimiento());

        return mapToResponse(usuarioRepository.save(usuario));
    }

    public void inicializarCarpetasRaizUsuario(Usuario usuario) {
        almacenamiento.inicializarCarpetasUsuario(usuario);
    }

    @Transactional
    public void subirImagenPerfil(Long usuarioId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Archivo vacío");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String rutaBD = almacenamiento.guardarImagenPerfil(usuario, file);
        usuario.setImagenPerfil(rutaBD);
        usuarioRepository.save(usuario);
    }

    private UsuarioPerfilResponse mapToResponse(Usuario usuario) {
        return new UsuarioPerfilResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getDniPasaporte(),
                usuario.getFechaNacimiento(),
                usuario.getRol().name(),
                usuario.getImagenPerfil()
        );
    }
}