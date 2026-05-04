package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.Reserva;
import com.apartmanagebackend.domain.Transaccion;
import com.apartmanagebackend.domain.Usuario;
import com.apartmanagebackend.domain.enums.EstadoReserva;
import com.apartmanagebackend.dto.transaccion.TransaccionRequest;
import com.apartmanagebackend.dto.transaccion.TransaccionResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ReservaRepository;
import com.apartmanagebackend.repository.TransaccionRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<TransaccionResponse> crearTransaccion(TransaccionRequest request, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();
        Apartamento apartamento = apartamentoRepository.findByIdAndPropietarioId(request.apartamentoId(), propietario.getId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));

        List<Transaccion> transaccionesGuardadas = new ArrayList<>();

        // CASO A: DIVIDIR ENTRE TODOS LOS INQUILINOS ACTIVOS
        if (request.dividirEntreTodos()) {
            List<Reserva> reservasActivas = reservaRepository.findAll().stream()
                    .filter(r -> r.getApartamento().getId().equals(apartamento.getId()))
                    .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                    .toList();

            if (reservasActivas.isEmpty()) {
                throw new RuntimeException("No hay inquilinos activos en este piso para dividir la transacción.");
            }

            // Dividir importe equitativamente (Redondeo a 2 decimales para evitar fallos contables)
            BigDecimal importeDividido = request.importe().divide(new BigDecimal(reservasActivas.size()), 2, RoundingMode.HALF_UP);

            for (Reserva res : reservasActivas) {
                Transaccion t = construirTransaccionBase(apartamento, res, request, importeDividido);
                transaccionesGuardadas.add(transaccionRepository.save(t));
            }
        }
        // CASO B: ASIGNAR A UN INQUILINO CONCRETO O GASTO GENERAL DEL PISO
        else {
            Reserva reserva = null;
            if (request.reservaId() != null) {
                reserva = reservaRepository.findById(request.reservaId()).orElseThrow();
            }
            Transaccion t = construirTransaccionBase(apartamento, reserva, request, request.importe());
            transaccionesGuardadas.add(transaccionRepository.save(t));
        }

        return transaccionesGuardadas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Para que el Propietario vea las transacciones de un piso concreto
    public List<TransaccionResponse> obtenerPorApartamento(Long apartamentoId) {
        return transaccionRepository.findByApartamentoIdOrderByFechaEmisionDesc(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Para que el Inquilino vea sus deudas/pagos
    public List<TransaccionResponse> obtenerPorReserva(Long reservaId) {
        return transaccionRepository.findByReservaIdOrderByFechaEmisionDesc(reservaId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> obtenerFiltradas(Long apartamentoId, String periodo, String emailPropietario) {

        // Calculamos las fechas en base al filtro que envía Angular
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = LocalDate.of(2000, 1, 1);
        LocalDate fechaFin = LocalDate.of(2100, 12, 31);

        if (periodo != null) {
            switch (periodo) {
                case "MES":
                    fechaInicio = hoy.minusDays(30);
                    break;
                case "TRIMESTRE":
                    fechaInicio = hoy.minusMonths(3);
                    break;
                case "ANIO_ACTUAL":
                    fechaInicio = hoy.withDayOfYear(1);
                    break;
                case "SIEMPRE":
                default:
                    break;
            }
        }

        // Buscamos en base de datos usando nuestro nuevo método del Repositorio
        List<Transaccion> transaccionesCrudas = transaccionRepository.buscarFiltradasSeguras(
                apartamentoId,
                emailPropietario,
                fechaInicio,
                fechaFin
        );

        // Convertimos las entidades a DTOs (TransaccionResponse) para mandarlas a Angular
        return transaccionesCrudas.stream()
                .map(this::mapToResponse) // Asumo que ya tienes este método de mapeo creado de antes
                .collect(Collectors.toList());
    }

    // Método auxiliar para no repetir código al construir la transacción
    private Transaccion construirTransaccionBase(Apartamento apto, Reserva reserva, TransaccionRequest req, BigDecimal importe) {
        return Transaccion.builder()
                .apartamento(apto)
                .reserva(reserva)
                .tipo(req.tipo())
                .categoria(req.categoria())
                .estado(req.estado())
                .concepto(req.concepto())
                .importe(importe)
                .comentario(req.comentario())
                .fechaEmision(req.fechaEmision() != null ? req.fechaEmision() : java.time.LocalDate.now())
                .fechaVencimiento(req.fechaVencimiento())
                .build();
    }

    private TransaccionResponse mapToResponse(Transaccion t) {

        String nombreApto = t.getApartamento() != null ? t.getApartamento().getNombreInterno() : null;

        String nombreInq = null;
        if (t.getReserva() != null && t.getReserva().getInquilino() != null) {
            nombreInq = t.getReserva().getInquilino().getNombre() + " " + t.getReserva().getInquilino().getApellidos();
        }

        return new TransaccionResponse(
                t.getId(),
                t.getApartamento() != null ? t.getApartamento().getId() : null,
                nombreApto,
                t.getReserva() != null ? t.getReserva().getId() : null,
                nombreInq,
                t.getTipo(),
                t.getCategoria(),
                t.getEstado(),
                t.getConcepto(),
                t.getImporte(),
                t.getComentario(),
                t.getFechaEmision(),
                t.getFechaVencimiento(),
                t.getFechaPago()
        );
    }
}