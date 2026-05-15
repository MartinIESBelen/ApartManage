package com.habitalis.service;

import com.habitalis.domain.TokenRecuperacion;
import com.habitalis.domain.Usuario;
import com.habitalis.repository.TokenRecuperacionRepository;
import com.habitalis.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecuperacionPasswordService {

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void solicitarRecuperacion(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            tokenRepository.deleteByUsuarioId(usuario.getId());

            String token = UUID.randomUUID().toString();

            TokenRecuperacion tokenEntity = TokenRecuperacion.builder()
                    .token(token)
                    .usuario(usuario)
                    .expiracion(LocalDateTime.now().plusMinutes(15))
                    .build();

            tokenRepository.save(tokenEntity);
            emailService.enviarEmailRecuperacion(email, token);
        });
    }

    @Transactional
    public void resetearPassword(String token, String nuevaPassword) {
        TokenRecuperacion tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (tokenEntity.getExpiracion().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(tokenEntity);
            throw new RuntimeException("El token ha expirado");
        }

        Usuario usuario = tokenEntity.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        tokenRepository.delete(tokenEntity);
    }
}