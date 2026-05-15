package com.habitalis.service;

import com.habitalis.domain.Transaccion;
import com.habitalis.domain.Usuario;
import com.habitalis.domain.enums.EstadoTransaccion;
import com.habitalis.domain.enums.EstadoIncidencia;
import com.habitalis.domain.enums.EstadoContrato;
import com.habitalis.domain.enums.TipoTransaccion;
import com.habitalis.dto.stats.DashboardStatsResponse;
import com.habitalis.dto.stats.FinanzasMesResponse;
import com.habitalis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ApartamentoRepository apartamentoRepository;
    private final TransaccionRepository transaccionRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;

    public DashboardStatsResponse obtenerResumen(String emailPropietario) {
        Usuario prop = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Long idPropietario = prop.getId();
        LocalDate hoy = LocalDate.now();

        long totalAptos = apartamentoRepository.findByPropietarioId(idPropietario).size();

        long ocupados = contratoRepository.findAll().stream()
                .filter(r -> r.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(r -> r.getEstado() == EstadoContrato.CONFIRMADA)
                .filter(r -> !hoy.isBefore(r.getFechaEntrada()) && !hoy.isAfter(r.getFechaSalida()))
                .map(r -> r.getApartamento().getId())
                .distinct()
                .count();

        BigDecimal ingresos = transaccionRepository.findAll().stream()
                .filter(t -> t.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                .filter(t -> t.getEstado() == EstadoTransaccion.PAGADO)
                .filter(t -> t.getFechaEmision().getMonthValue() == hoy.getMonthValue() && t.getFechaEmision().getYear() == hoy.getYear())
                .map(Transaccion::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deuda = transaccionRepository.findAll().stream()
                .filter(t -> t.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                .filter(t -> t.getEstado() == EstadoTransaccion.PENDIENTE || t.getEstado() == EstadoTransaccion.VENCIDO)
                .map(Transaccion::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long incidencias = incidenciaRepository.findAll().stream()
                .filter(i -> i.getApartamento().getPropietario().getId().equals(idPropietario))
                .filter(i -> i.getEstado() != EstadoIncidencia.SOLUCIONADA)
                .count();

        return new DashboardStatsResponse(totalAptos, ocupados, ingresos, deuda, incidencias);
    }

    public List<FinanzasMesResponse> obtenerBalanceConsolidado(String emailPropietario, Integer anio) {
        Usuario prop = usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        List<Transaccion> transaccionesAnio = transaccionRepository.findAll().stream()
                .filter(t -> t.getApartamento().getPropietario().getId().equals(prop.getId()))
                .filter(t -> t.getFechaEmision().getYear() == anio)
                .toList();

        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        List<FinanzasMesResponse> balanceAnual = new java.util.ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            final int mesActual = i;

            BigDecimal ingresosMes = transaccionesAnio.stream()
                    .filter(t -> t.getTipo() == TipoTransaccion.INGRESO)
                    .filter(t -> t.getEstado() == EstadoTransaccion.PAGADO)
                    .filter(t -> t.getFechaEmision().getMonthValue() == mesActual)
                    .map(Transaccion::getImporte)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal gastosMes = transaccionesAnio.stream()
                    .filter(t -> t.getTipo() == TipoTransaccion.GASTO)
                    .filter(t -> t.getFechaEmision().getMonthValue() == mesActual)
                    .map(Transaccion::getImporte)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            balanceAnual.add(new FinanzasMesResponse(nombresMeses[i - 1], ingresosMes, gastosMes));
        }

        return balanceAnual;
    }
}