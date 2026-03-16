package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Apartamento;
import com.example.apartmanagebackend.domain.Inquilino;
import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.domain.Reserva;
import com.example.apartmanagebackend.domain.enums.EstadoReserva;
import com.example.apartmanagebackend.dto.reserva.ReservaRequest;
import com.example.apartmanagebackend.dto.reserva.ReservaResponse;
import com.example.apartmanagebackend.dto.reserva.VincularRequest;
import com.example.apartmanagebackend.repository.ApartamentoRepository;
import com.example.apartmanagebackend.repository.ReservaRepository;
import com.example.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;

    // EL PROPIETARIO CREA LA RESERVA Y GENERA EL CÓDIGO
    public ReservaResponse crearReserva(Long apartamentoId, ReservaRequest request, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));

        // Generamos un código único de 8 caracteres en mayúsculas
        String codigoGenerado = "APT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Reserva nuevaReserva = Reserva.builder()
                .apartamento(apartamento)
                .codigoVinculacion(codigoGenerado)
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .precioBaseAlquiler(request.precioBaseAlquiler())
                .estado(EstadoReserva.PENDIENTE) // Esperando a que un inquilino use el código
                .build();

        return mapToResponse(reservaRepository.save(nuevaReserva));
    }

    //EL INQUILINO INTRODUCE EL CÓDIGO Y SE VINCULA
    public ReservaResponse vincularInquilino(VincularRequest request, String emailInquilino) {
        Inquilino inquilino = (Inquilino) usuarioRepository.findByEmail(emailInquilino)
                .orElseThrow(() -> new RuntimeException("Solo los inquilinos pueden vincularse"));

        // Buscamos la reserva por el código secreto
        Reserva reserva = reservaRepository.findByCodigoVinculacion(request.codigoVinculacion())
                .orElseThrow(() -> new RuntimeException("Código de vinculación inválido o no existe"));

        // Verificamos que la reserva esté libre (PENDIENTE)
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Este código ya ha sido usado o la reserva no está disponible");
        }

        reserva.setInquilino(inquilino);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return mapToResponse(reservaRepository.save(reserva));
    }

    // OBTENER LA LISTA DE RESERVAS DE UN APARTAMENTO (Para el propietario)
    public List<ReservaResponse> listarReservasPorApartamento(Long apartamentoId, String emailPropietario) {
        // Comprobamos si es realmente el propietario del piso
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        boolean esSuPiso = apartamentoRepository.findById(apartamentoId)
                .map(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElse(false);

        if (!esSuPiso) {
            throw new RuntimeException("No tienes permisos para ver las reservas de este apartamento");
        }

        return reservaRepository.findByApartamentoId(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReservaResponse mapToResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getCodigoVinculacion(),
                reserva.getFechaEntrada(),
                reserva.getFechaSalida(),
                reserva.getPrecioBaseAlquiler(),
                reserva.getEstado(),
                reserva.getApartamento().getNombreInterno(),
                reserva.getInquilino() != null ? reserva.getInquilino().getNombreCompleto() : "Sin asignar"
        );
    }
}
