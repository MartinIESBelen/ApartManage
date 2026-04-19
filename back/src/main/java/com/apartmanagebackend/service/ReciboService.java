package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Propietario;
import com.apartmanagebackend.domain.Recibo;
import com.apartmanagebackend.domain.Reserva;
import com.apartmanagebackend.domain.Usuario;
import com.apartmanagebackend.domain.enums.EstadoRecibo;
import com.apartmanagebackend.domain.enums.MetodoPago;
import com.apartmanagebackend.dto.recibo.ReciboRequest;
import com.apartmanagebackend.dto.recibo.ReciboResponse;
import com.apartmanagebackend.repository.ReciboRepository;
import com.apartmanagebackend.repository.ReservaRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReciboService {

    private final ReciboRepository reciboRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReciboResponse crearRecibo(Long reservaId, ReciboRequest request, String emailPropietario) {

        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Reserva reserva = reservaRepository.findById(reservaId)
                .filter(r -> r.getApartamento().getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada o acceso denegado"));

        BigDecimal total = request.montoAlquiler()
                .add(request.montoLuz())
                .add(request.montoAgua());

        Recibo nuevoRecibo = Recibo.builder()
                .reserva(reserva)
                .mes(request.mes())
                .anio(request.anio())
                .montoAlquiler(request.montoAlquiler())
                .montoLuz(request.montoLuz())
                .montoAgua(request.montoAgua())
                .totalPagar(total)
                .estado(EstadoRecibo.PENDIENTE)
                .metodoPago(MetodoPago.NO_ESPECIFICADO)
                .build();

        return mapToResponse(reciboRepository.save(nuevoRecibo));
    }

    public List<ReciboResponse> obtenerRecibosPorReserva(Long reservaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();

        boolean esPropietario = reserva.getApartamento().getPropietario().getId().equals(usuario.getId());
        boolean esInquilino = reserva.getInquilino() != null && reserva.getInquilino().getId().equals(usuario.getId());

        if (!esPropietario && !esInquilino) {
            throw new RuntimeException("No tienes permisos para ver estos recibos");
        }

        return reciboRepository.findByReservaId(reservaId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReciboResponse pagarRecibo(Long reciboId, MetodoPago metodo, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino)
                .orElseThrow(() -> new RuntimeException("Inquilino no encontrado"));

        Recibo recibo = reciboRepository.findById(reciboId)
                .filter(r -> r.getReserva().getInquilino() != null &&
                        r.getReserva().getInquilino().getId().equals(inquilino.getId()))
                .orElseThrow(() -> new RuntimeException("Recibo no encontrado o no te pertenece"));

        recibo.setEstado(EstadoRecibo.PAGADO);
        recibo.setMetodoPago(metodo);
        recibo.setFechaPago(LocalDate.now());

        return mapToResponse(reciboRepository.save(recibo));
    }

    public List<ReciboResponse> obtenerRecibosPorEstado(EstadoRecibo estado) {
        return reciboRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReciboResponse mapToResponse(Recibo recibo) {
        return new ReciboResponse(
                recibo.getId(),
                recibo.getMes(),
                recibo.getAnio(),
                recibo.getMontoAlquiler(),
                recibo.getMontoLuz(),
                recibo.getMontoAgua(),
                recibo.getTotalPagar(),
                recibo.getEstado(),
                recibo.getFechaPago(),
                recibo.getReserva().getId()
        );
    }
}