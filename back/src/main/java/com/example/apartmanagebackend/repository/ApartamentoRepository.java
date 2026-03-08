package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento,Long> {
    List<Apartamento> findByPropietarioId(Long propietarioId);

    Optional<Apartamento> findByIdAndPropietarioId(Long id, Long propietarioId);
}
