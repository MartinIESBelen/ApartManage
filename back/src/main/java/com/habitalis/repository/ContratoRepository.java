package com.habitalis.repository;

import com.habitalis.domain.Contrato;
import com.habitalis.domain.enums.EstadoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato,Long> {
    Optional<Contrato> findByCodigoVinculacion(String codigoVinculacion);

    List<Contrato> findByInquilinoId(Long inquilinoId);

    List<Contrato> findByApartamentoId(Long apartamentoId);

    boolean existsByApartamentoIdAndInquilinoIdAndEstado(Long apartamentoId, Long inquilinoId, EstadoContrato estado);

    @Query("SELECT c FROM Contrato c JOIN c.apartamento a WHERE c.id = :contratoId AND a.propietario.email = :email")
    Optional<Contrato> findByIdAndPropietarioEmail(@Param("contratoId") Long contratoId, @Param("email") String email);

    @Query("SELECT c FROM Contrato c JOIN c.apartamento a WHERE a.propietario.email = :email ORDER BY c.id DESC")
    List<Contrato> findMisContratosComoPropietario(@Param("email") String email);
}