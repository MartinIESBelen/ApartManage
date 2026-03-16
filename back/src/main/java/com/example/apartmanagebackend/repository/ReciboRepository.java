package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Recibo;
import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReciboRepository extends JpaRepository<Recibo,Long> {

    List<Recibo> findByReservaId(Long reservaId);

    List<Recibo> findByEstado(EstadoRecibo estado);
}
