package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento,Long> {
    List<Apartamento> findByPropietarioId(Long propietarioId);

    Optional<Apartamento> findByIdAndPropietarioId(Long id, Long propietarioId);

    @Query("SELECT COUNT(c) > 0 FROM Contrato c WHERE c.apartamento.id = :aptoId " +
            "AND c.inquilino.id = :inquilinoId AND c.estado = 'CONFIRMADA'")
    boolean esInquilinoActivo(@Param("aptoId") Long aptoId, @Param("inquilinoId") Long inquilinoId);
}
