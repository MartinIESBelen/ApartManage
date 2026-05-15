package com.habitalis.service;

import com.habitalis.domain.Apartamento;
import com.habitalis.domain.Incidencia;
import com.habitalis.domain.Usuario;
import com.habitalis.dto.incidencia.IncidenciaRequest;
import com.habitalis.dto.incidencia.IncidenciaResponse;
import com.habitalis.repository.ApartamentoRepository;
import com.habitalis.repository.IncidenciaRepository;
import com.habitalis.repository.UsuarioRepository;
import com.habitalis.domain.enums.EstadoIncidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidenciaService {
    private final IncidenciaRepository incidenciaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public IncidenciaResponse reportarIncidencia(IncidenciaRequest request, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Apartamento apto = apartamentoRepository.findById(request.apartamentoId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        Incidencia nueva = Incidencia.builder()
                .apartamento(apto)
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .build();

        return mapToResponse(incidenciaRepository.save(nueva));
    }

    public IncidenciaResponse cambiarEstado(Long id, EstadoIncidencia nuevoEstado, String emailPropietario) {
        Incidencia incidencia = incidenciaRepository.findById(id).orElseThrow();

        if (!incidencia.getApartamento().getPropietario().getEmail().equals(emailPropietario)) {
            throw new RuntimeException("No tienes permiso sobre esta incidencia");
        }

        incidencia.setEstado(nuevoEstado);
        return mapToResponse(incidenciaRepository.save(incidencia));
    }

    private IncidenciaResponse mapToResponse(Incidencia incidencia) {
        return new IncidenciaResponse(
                incidencia.getId(), incidencia.getTitulo(), incidencia.getDescripcion(),
                incidencia.getEstado(), incidencia.getFechaReporte(),
                incidencia.getApartamento().getNombreInterno()
        );
    }
}