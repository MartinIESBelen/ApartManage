package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.config.JwtService;
import com.example.apartmanagebackend.domain.Inquilino;
import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.domain.Usuario;
import com.example.apartmanagebackend.domain.enums.RolUsuario;
import com.example.apartmanagebackend.dto.auth.AuthResponse;
import com.example.apartmanagebackend.dto.auth.LoginRequest;
import com.example.apartmanagebackend.dto.auth.RegisterRequest;
import com.example.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Validar si el email ya existe
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario user;

        if (request.rol() == RolUsuario.PROPIETARIO) {
            user = Propietario.builder()
                    .nombreCompleto(request.nombreCompleto())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .rol(RolUsuario.PROPIETARIO)
                    .build();
        } else {
            // Asumimos Inquilino por defecto si no es propietario
            user = Inquilino.builder()
                    .nombreCompleto(request.nombreCompleto())
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .rol(RolUsuario.INQUILINO)
                    .build();
        }

        // Guardar en BD
        usuarioRepository.save(user);

        // Generar Token
        var jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        // Autenticar (Esto verifica usuario y contraseña automáticamente)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Buscamos al usuario.
        var user = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        // Generamos Token
        var jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken);
    }
}
