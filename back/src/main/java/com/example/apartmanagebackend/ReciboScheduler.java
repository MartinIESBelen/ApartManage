package com.example.apartmanagebackend;

import com.example.apartmanagebackend.domain.Recibo;
import com.example.apartmanagebackend.domain.Reserva;
import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import com.example.apartmanagebackend.domain.enums.EstadoReserva;
import com.example.apartmanagebackend.repository.ReciboRepository;
import com.example.apartmanagebackend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j // Para ver mensajes en la consola
public class ReciboScheduler {

    private final ReservaRepository reservaRepository;
    private final ReciboRepository reciboRepository;

    /**
     * Se ejecuta el día 1 de cada mes a las 00:00:00
     * Expresión cron: Segundos Minutos Horas Día Mes Día_Semana
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void generarRecibosMensuales() {
        log.info("Iniciando generación automática de recibos mensuales...");

        LocalDate hoy = LocalDate.now();

        // 1. Buscamos reservas CONFIRMADAS donde hoy esté dentro del rango de fechas
        List<Reserva> reservasActivas = reservaRepository.findAll().stream()
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> !hoy.isBefore(r.getFechaEntrada()) && !hoy.isAfter(r.getFechaSalida()))
                .toList();

        int creados = 0;
        for (Reserva reserva : reservasActivas) {
            // 2. Opcional: Verificar si ya existe un recibo para esta reserva en este mes/año
            // (Para evitar duplicados si reinicias el servidor el día 1)

            Recibo nuevoRecibo = Recibo.builder()
                    .reserva(reserva)
                    .mes(hoy.getMonthValue())
                    .anio(hoy.getYear())
                    .montoAlquiler(reserva.getPrecioBaseAlquiler())
                    .montoLuz(BigDecimal.ZERO) // Se edita luego manualmente
                    .montoAgua(BigDecimal.ZERO) // Se edita luego manualmente
                    .totalPagar(reserva.getPrecioBaseAlquiler())
                    .estado(EstadoRecibo.PENDIENTE)
                    .build();

            reciboRepository.save(nuevoRecibo);
            creados++;
        }

        log.info("Proceso finalizado. Se han generado {} recibos.", creados);
    }
}