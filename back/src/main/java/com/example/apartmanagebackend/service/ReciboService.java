package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.domain.Recibo;
import com.example.apartmanagebackend.domain.Reserva;
import com.example.apartmanagebackend.domain.Usuario;
import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import com.example.apartmanagebackend.dto.recibo.ReciboRequest;
import com.example.apartmanagebackend.dto.recibo.ReciboResponse;
import com.example.apartmanagebackend.repository.ReciboRepository;
import com.example.apartmanagebackend.repository.ReservaRepository;
import com.example.apartmanagebackend.repository.UsuarioRepository;
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

    // CREAR RECIBO
    public ReciboResponse crearRecibo(Long reservaId, ReciboRequest request, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        Reserva reserva = reservaRepository.findById(reservaId)
                .filter(r -> r.getApartamento().getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada o no tienes permisos"));

        // Calculamos el total automáticamente
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
                .totalPagar(total) // Guardamos la suma
                .estado(EstadoRecibo.PENDIENTE) // O el estado inicial que tú prefieras
                .build();

        return mapToResponse(reciboRepository.save(nuevoRecibo));
    }

    // VER RECIBOS DE UNA RESERVA (Propietarios e Inquilinos)
    public List<ReciboResponse> obtenerRecibosPorReserva(Long reservaId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();
        Reserva reserva = reservaRepository.findById(reservaId).orElseThrow();

        // Seguridad: ¿Es el dueño del piso o es el inquilino que vive ahí?
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

    // PAGAR RECIBO (Acción del inquilino - Simulación)
    public ReciboResponse pagarRecibo(Long reciboId, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino).orElseThrow();

        Recibo recibo = reciboRepository.findById(reciboId)
                .filter(r -> r.getReserva().getInquilino() != null && r.getReserva().getInquilino().getId().equals(inquilino.getId()))
                .orElseThrow(() -> new RuntimeException("Recibo no encontrado o no te pertenece"));

        recibo.setEstado(EstadoRecibo.PAGADO); // Ajusta el nombre al de tu enum
        recibo.setFechaPago(LocalDate.now()); // Registramos que se ha pagado HOY

        return mapToResponse(reciboRepository.save(recibo));
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