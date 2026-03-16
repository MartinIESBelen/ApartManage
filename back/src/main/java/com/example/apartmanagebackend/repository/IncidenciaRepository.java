package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByApartamentoId(Long apartamentoId);
}