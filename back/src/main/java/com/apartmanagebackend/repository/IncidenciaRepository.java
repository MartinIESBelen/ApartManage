package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByApartamentoId(Long apartamentoId);
}