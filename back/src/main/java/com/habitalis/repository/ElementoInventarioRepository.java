package com.habitalis.repository;

import com.habitalis.domain.ElementoInventario;
import com.habitalis.domain.enums.EstadoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementoInventarioRepository extends JpaRepository<ElementoInventario,Long> {
    List<ElementoInventario> findByApartamentoId(Long apartamentoId);

    boolean existsByApartamentoIdAndEstado(Long apartamentoId, EstadoItem estado);
}
