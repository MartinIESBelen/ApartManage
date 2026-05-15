package com.habitalis.repository;

import com.habitalis.domain.Incidencia;
import com.habitalis.domain.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByApartamentoId(Long apartamentoId);

    boolean existsByApartamentoIdAndEstado(Long apartamentoId, EstadoIncidencia estado);
}