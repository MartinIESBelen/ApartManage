package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByApartamentoId(Long apartamentoId);
    List<Gasto> findByApartamentoPropietarioId(Long propietarioId);
}
