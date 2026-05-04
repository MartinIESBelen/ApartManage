package com.apartmanagebackend;

import com.apartmanagebackend.domain.Reserva;
import com.apartmanagebackend.domain.Transaccion;
import com.apartmanagebackend.domain.enums.CategoriaTransaccion;
import com.apartmanagebackend.domain.enums.EstadoReserva;
import com.apartmanagebackend.domain.enums.EstadoTransaccion;
import com.apartmanagebackend.domain.enums.TipoTransaccion;
import com.apartmanagebackend.repository.ReservaRepository;
import com.apartmanagebackend.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransaccionScheduler {

    private final ReservaRepository reservaRepository;
    private final TransaccionRepository transaccionRepository;

    /**
     * TAREA 1: GENERAR COBROS
     * Se ejecuta el día 1 de cada mes a las 00:00:00
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void generarAlquileresMensuales() {
        log.info("Iniciando generación automática de alquileres mensuales...");
        LocalDate hoy = LocalDate.now();

        List<Reserva> reservasActivas = reservaRepository.findAll().stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> !hoy.isBefore(r.getFechaEntrada()) && !hoy.isAfter(r.getFechaSalida()))
                .toList();

        int creados = 0;
        for (Reserva reserva : reservasActivas) {
            Transaccion cobroAlquiler = Transaccion.builder()
                    .apartamento(reserva.getApartamento())
                    .reserva(reserva)
                    .tipo(TipoTransaccion.INGRESO)
                    .categoria(CategoriaTransaccion.ALQUILER)
                    .estado(EstadoTransaccion.PENDIENTE)
                    .concepto("Alquiler " + hoy.getMonthValue() + "/" + hoy.getYear())
                    .importe(reserva.getPrecioBaseAlquiler())
                    .fechaEmision(hoy)
                    .fechaVencimiento(hoy.plusDays(5))
                    .build();

            transaccionRepository.save(cobroAlquiler);
            creados++;
        }
        log.info("Se han generado {} transacciones de alquiler.", creados);
    }

    /**
     * TAREA 2: ALERTA PREVENTIVA
     * Se ejecuta el día 16 de cada mes a las 00:01
     */
    @Scheduled(cron = "0 1 0 16 * *")
    public void detectarRetrasosQuincena() {
        log.info("Ejecutando vigilancia del día 15...");
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = LocalDate.of(hoy.getYear(), hoy.getMonth(), 1);

        List<Transaccion> retrasados = transaccionRepository.findByTipoAndCategoriaAndEstadoAndFechaEmisionBefore(
                        TipoTransaccion.INGRESO, CategoriaTransaccion.ALQUILER, EstadoTransaccion.PENDIENTE, hoy)
                .stream()
                .filter(t -> t.getFechaEmision().getMonth() == inicioMes.getMonth() && t.getFechaEmision().getYear() == inicioMes.getYear())
                .toList();

        if (!retrasados.isEmpty()) {
            log.warn("¡ALERTA! Se han detectado {} alquileres sin pagar a día 15.", retrasados.size());
        }
    }

    /**
     * TAREA 3: MARCAR COMO VENCIDOS
     * Se ejecuta el día 1 de cada mes a las 00:05 (Justo después de generar los nuevos)
     */
    @Scheduled(cron = "0 5 0 1 * *")
    public void marcarRecibosVencidos() {
        log.info("Marcando alquileres antiguos como VENCIDOS...");
        LocalDate hoy = LocalDate.now();

        List<Transaccion> caducados = transaccionRepository.findByTipoAndCategoriaAndEstadoAndFechaEmisionBefore(
                TipoTransaccion.INGRESO, CategoriaTransaccion.ALQUILER, EstadoTransaccion.PENDIENTE, hoy);

        int actualizados = 0;
        for (Transaccion t : caducados) {
            t.setEstado(EstadoTransaccion.VENCIDO);
            transaccionRepository.save(t);
            actualizados++;
        }

        if (actualizados > 0) {
            log.warn("Se han movido {} transacciones al estado VENCIDO.", actualizados);
        }
    }
}