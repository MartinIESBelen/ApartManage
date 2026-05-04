package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    Optional<Reserva> findByCodigoVinculacion(String codigoVinculacion);

    List<Reserva> findByInquilinoId(Long inquilinoId);

    List<Reserva> findByApartamentoId(Long apartamentoId);

    @Query("SELECT r FROM Reserva r JOIN r.apartamento a WHERE r.id = :reservaId AND a.propietario.email = :email")
    Optional<Reserva> findByIdAndPropietarioEmail(@Param("reservaId") Long reservaId, @Param("email") String email);

    @Query("SELECT r FROM Reserva r JOIN r.apartamento a WHERE a.propietario.email = :email ORDER BY r.id DESC")
    List<Reserva> findMisContratosComoPropietario(@Param("email") String email);
}
