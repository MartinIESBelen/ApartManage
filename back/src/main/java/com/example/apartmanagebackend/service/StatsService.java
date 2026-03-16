package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.domain.Recibo;
import com.example.apartmanagebackend.domain.Reserva;
import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import com.example.apartmanagebackend.domain.enums.EstadoIncidencia;
import com.example.apartmanagebackend.domain.enums.EstadoReserva;
import com.example.apartmanagebackend.dto.stats.DashboardStatsResponse;
import com.example.apartmanagebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final ApartamentoRepository apartamentoRepository;
    private final ReciboRepository reciboRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository; // Inyectamos también reservas

    public DashboardStatsResponse obtenerResumen(String emailPropietario) {
        Propietario prop = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));
        Long idPropietario = prop.getId();
        LocalDate hoy = LocalDate.now();

        // Total de apartamentos del propietario
        long totalAptos = apartamentoRepository.findByPropietarioId(idPropietario).size();

        // Cálculo de apartamentos OCUPADOS actualmente
        // Se considera ocupado si tiene una reserva CONFIRMADA y hoy está entre entrada y salida
        long ocupados = reservaRepository.findAll().stream()
                .filter(r -> r.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> !hoy.isBefore(r.getFechaEntrada()) && !hoy.isAfter(r.getFechaSalida()))
                .map(r -> r.getApartamento().getId())
                .distinct() // Evitamos contar doble si hubiera solapamientos raros
                .count();

        // Ingresos del mes actual (Pagados)
        BigDecimal ingresos = reciboRepository.findAll().stream()
                .filter(r -> r.getReserva().getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(r -> r.getEstado() == EstadoRecibo.PAGADO)
                .filter(r -> r.getMes() == hoy.getMonthValue() && r.getAnio() == hoy.getYear())
                .map(Recibo::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Deuda pendiente total
        BigDecimal deuda = reciboRepository.findAll().stream()
                .filter(r -> r.getReserva().getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(r -> r.getEstado() == EstadoRecibo.PENDIENTE || r.getEstado() == EstadoRecibo.VENCIDO)
                .map(Recibo::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Incidencias abiertas
        long incidencias = incidenciaRepository.findAll().stream()
                .filter(i -> i.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(i -> i.getEstado() != EstadoIncidencia.SOLUCIONADA)
                .count();

        return new DashboardStatsResponse(totalAptos, ocupados, ingresos, deuda, incidencias);
    }
}