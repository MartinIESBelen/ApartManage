package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Transaccion;
import com.apartmanagebackend.domain.enums.CategoriaTransaccion;
import com.apartmanagebackend.domain.enums.EstadoTransaccion;
import com.apartmanagebackend.domain.enums.TipoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion,Long> {

    // Para ver todas las transacciones de un piso concreto
    List<Transaccion> findByApartamentoIdOrderByFechaEmisionDesc(Long apartamentoId);

    // Para ver las transacciones que le corresponden a un inquilino
    List<Transaccion> findByContratoIdOrderByFechaEmisionDesc(Long contratoId);

    // Para calcular ingresos/gastos globales del propietario en el Dashboard
    @Query("SELECT t FROM Transaccion t WHERE t.apartamento.propietario.id = :propietarioId AND t.tipo = :tipo AND YEAR(t.fechaEmision) = :anio")
    List<Transaccion> findByPropietarioIdAndTipoAndAnio(Long propietarioId, TipoTransaccion tipo, Integer anio);

    List<Transaccion> findByTipoAndCategoriaAndEstadoAndFechaEmisionBefore(
            TipoTransaccion tipo,
            CategoriaTransaccion categoria,
            EstadoTransaccion estado,
            java.time.LocalDate fechaLimite
    );

    @Query("SELECT t FROM Transaccion t JOIN t.apartamento a " +
            "WHERE a.propietario.email = :email " +
            "AND (:apartamentoId IS NULL OR a.id = :apartamentoId) " +
            "AND t.fechaEmision >= :fechaInicio " +
            "AND t.fechaEmision <= :fechaFin " +
            "ORDER BY t.fechaEmision DESC")
    List<Transaccion> buscarFiltradasSeguras(
            @Param("apartamentoId") Long apartamentoId,
            @Param("email") String email,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
