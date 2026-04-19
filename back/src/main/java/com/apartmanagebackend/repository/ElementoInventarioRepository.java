package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.ElementoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElementoInventarioRepository extends JpaRepository<ElementoInventario,Long> {
    List<ElementoInventario> findByApartamentoId(Long apartamentoId);
}
