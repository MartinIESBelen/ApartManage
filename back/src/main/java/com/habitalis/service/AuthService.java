package com.habitalis.service;

import com.habitalis.config.JwtService;
import com.habitalis.domain.Usuario;
import com.habitalis.domain.enums.RolUsuario;
import com.habitalis.dto.auth.AuthResponse;
import com.habitalis.dto.auth.LoginRequest;
import com.habitalis.dto.auth.RefreshTokenRequest;
import com.habitalis.dto.auth.RegisterRequest;
import com.habitalis.repository.UsuarioRepository;
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
    private final UsuarioService usuarioService;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (request.rol() == RolUsuario.ADMIN) {
            throw new RuntimeException("No está permitido registrar cuentas de Administrador desde esta vía.");
        }

        if (request.fechaNacimiento() != null) {
            int edad = java.time.Period.between(request.fechaNacimiento(), java.time.LocalDate.now()).getYears();
            if (edad < 18) {
                throw new RuntimeException("Debes ser mayor de 18 años para registrarte en la plataforma.");
            }
        }

        Usuario user = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .dniPasaporte(request.dniPasaporte())
                .fechaNacimiento(request.fechaNacimiento())
                .rol(request.rol())
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(user);
        usuarioService.inicializarCarpetasRaizUsuario(usuarioGuardado);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                jwtToken,
                refreshToken,
                "Bearer",
                jwtService.getJwtExpiration() / 1000
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                jwtToken,
                refreshToken,
                "Bearer",
                jwtService.getJwtExpiration() / 1000
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            Usuario user = usuarioRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);

                return new AuthResponse(
                        accessToken,
                        refreshToken,
                        "Bearer",
                        jwtService.getJwtExpiration() / 1000
                );
            }
        }
        throw new RuntimeException("Refresh accessToken inválido o caducado");
    }
}
