package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    Optional<Reserva> findByCodigoVinculacion(String codigoVinculacion);

    List<Reserva> findByInquilinoId(Long inquilinoId);

    List<Reserva> findByApartamentoId(Long apartamentoId);

}
