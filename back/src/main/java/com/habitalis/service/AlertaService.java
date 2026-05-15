package com.habitalis.service;

import com.habitalis.domain.enums.EstadoIncidencia;
import com.habitalis.domain.enums.EstadoItem;
import com.habitalis.domain.enums.TipoAlerta;
import com.habitalis.repository.ElementoInventarioRepository;
import com.habitalis.repository.IncidenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final ElementoInventarioRepository inventarioRepository;
    private final IncidenciaRepository incidenciaRepository;

    public Set<TipoAlerta> evaluarAlertasApartamento(Long apartamentoId) {
        Set<TipoAlerta> alertasActivas = new HashSet<>();

        if (inventarioRepository.existsByApartamentoIdAndEstado(apartamentoId, EstadoItem.ROTO)) {
            alertasActivas.add(TipoAlerta.INVENTARIO_ROTO);
        }

        if (incidenciaRepository.existsByApartamentoIdAndEstado(apartamentoId, EstadoIncidencia.ABIERTA)) {
            alertasActivas.add(TipoAlerta.NUEVA_INCIDENCIA);
        }

        return alertasActivas;
    }
}