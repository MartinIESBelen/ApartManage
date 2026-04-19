package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.Incidencia;
import com.apartmanagebackend.domain.Usuario;
import com.apartmanagebackend.dto.incidencia.IncidenciaRequest;
import com.apartmanagebackend.dto.incidencia.IncidenciaResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.IncidenciaRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import com.apartmanagebackend.domain.*;
import com.apartmanagebackend.domain.enums.EstadoIncidencia;
import com.apartmanagebackend.dto.incidencia.*;
import com.apartmanagebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidenciaService {
    private final IncidenciaRepository incidenciaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public IncidenciaResponse reportarIncidencia(IncidenciaRequest request, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino).orElseThrow();
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

        // Seguridad: ¿Es el dueño del apartamento?
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