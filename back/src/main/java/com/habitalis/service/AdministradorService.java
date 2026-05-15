package com.habitalis.service;

import com.habitalis.domain.Usuario;
import com.habitalis.domain.enums.RolUsuario;
import com.habitalis.dto.usuario.UsuarioGlobalResponse;
import com.habitalis.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioGlobalResponse> listarTodosLosUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UsuarioGlobalResponse cambiarEstadoBloqueo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() == RolUsuario.ADMIN) {
            throw new RuntimeException("No está permitido bloquear a un Administrador");
        }

        usuario.setBloqueado(!usuario.isBloqueado());

        return mapToResponse(usuarioRepository.save(usuario));
    }

    private UsuarioGlobalResponse mapToResponse(Usuario usuario) {
        return new UsuarioGlobalResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getRol(),
                usuario.isBloqueado()
        );
    }
}