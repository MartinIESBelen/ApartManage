package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.*;
import com.apartmanagebackend.domain.enums.EstadoReserva;
import com.apartmanagebackend.dto.reserva.*;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ReservaRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    // EL PROPIETARIO CREA LA RESERVA Y GENERA EL CÓDIGO
    public ReservaResponse crearReserva(Long apartamentoId, ReservaRequest request, String emailPropietario) {

        if (request.fechaSalida().isBefore(request.fechaEntrada()) || request.fechaSalida().isEqual(request.fechaEntrada())) {
            throw new RuntimeException("La fecha de salida debe ser estrictamente posterior a la de entrada.");
        }

        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));

        String codigoGenerado = "APT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Reserva nuevaReserva = Reserva.builder()
                .apartamento(apartamento)
                .codigoVinculacion(codigoGenerado)
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .precioBaseAlquiler(request.precioBaseAlquiler())
                .fianza(request.fianza())
                .estado(EstadoReserva.PENDIENTE)
                .build();

        return mapToResponse(reservaRepository.save(nuevaReserva));
    }

    // EL PROPIETARIO CREA UN CONTRATO MANUAL CON UN INQUILINO FANTASMA
    @Transactional
    public ReservaResponse crearReservaManual(Long apartamentoId, ReservaManualRequest request, String emailPropietario) {

        if (request.fechaSalida().isBefore(request.fechaEntrada()) || request.fechaSalida().isEqual(request.fechaEntrada())) {
            throw new RuntimeException("La fecha de salida debe ser estrictamente posterior a la de entrada.");
        }

        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));

        // buscar o crear al inquilino fantasma
        Usuario inquilino = usuarioRepository.findByEmail(request.emailInquilino()).orElseGet(() -> {
            String contraseñaFantasma = UUID.randomUUID().toString();

            Usuario nuevoInquilino = new Usuario();
            nuevoInquilino.setNombre(request.nombreInquilino());
            nuevoInquilino.setApellidos(request.apellidosInquilino());
            nuevoInquilino.setEmail(request.emailInquilino());
            nuevoInquilino.setPassword(passwordEncoder.encode(contraseñaFantasma));
            nuevoInquilino.setTelefono(request.telefonoInquilino());
            nuevoInquilino.setDniPasaporte(request.dniInquilino());
            nuevoInquilino.setFechaNacimiento(request.fechaNacimientoInquilino());
            nuevoInquilino.setRol(com.apartmanagebackend.domain.enums.RolUsuario.INQUILINO); // Se guarda como rol, pero es Usuario

            return usuarioRepository.save(nuevoInquilino);
        });

        String codigoInterno = "MANUAL-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Reserva nuevaReserva = Reserva.builder()
                .apartamento(apartamento)
                .inquilino(inquilino) // <-- Ya no hay casting a (Inquilino)
                .codigoVinculacion(codigoInterno)
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .precioBaseAlquiler(request.precioBaseAlquiler())
                .estado(EstadoReserva.CONFIRMADA)
                .build();

        return mapToResponse(reservaRepository.save(nuevaReserva));
    }

    // EL INQUILINO INTRODUCE EL CÓDIGO Y SE VINCULA
    public ReservaResponse vincularInquilino(VincularRequest request, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // <-- Ahora cualquiera puede vincularse a un piso

        Reserva reserva = reservaRepository.findByCodigoVinculacion(request.codigoVinculacion())
                .orElseThrow(() -> new RuntimeException("Código de vinculación inválido o no existe"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Este código ya ha sido usado o la reserva no está disponible");
        }

        reserva.setInquilino(inquilino);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return mapToResponse(reservaRepository.save(reserva));
    }

    // OBTENER LA LISTA DE RESERVAS DE UN APARTAMENTO
    public List<ReservaResponse> listarReservasPorApartamento(Long apartamentoId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();

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
                reserva.getInquilino() != null ? reserva.getInquilino().getNombre() + " " + reserva.getInquilino().getApellidos() : "Sin asignar"
        );
    }

    // Método para obtener la lista resumida de contratos
    public List<ContratoResponse> obtenerMisContratosPropietario(String emailPropietario) {
        return reservaRepository.findMisContratosComoPropietario(emailPropietario)
                .stream()
                .map(r -> new ContratoResponse(
                        r.getId(),
                        r.getCodigoVinculacion(),
                        r.getApartamento().getNombreInterno(),
                        r.getInquilino() != null ? r.getInquilino().getNombre() + " " + r.getInquilino().getApellidos() : "Sin asignar",
                        r.getEstado()
                )).toList();
    }

    public ContratoDetalleResponse obtenerDetalleContrato(Long reservaId, String emailPropietario) {
        // 1. Buscamos la reserva de forma segura
        Reserva r = reservaRepository.findByIdAndPropietarioEmail(reservaId, emailPropietario)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado o no tienes permisos"));

        // 2. Mapeamos los datos del inquilino (si existe)
        ContratoDetalleResponse.InquilinoPublico inquilinoData = null;
        if (r.getInquilino() != null) {
            inquilinoData = new ContratoDetalleResponse.InquilinoPublico(
                    r.getInquilino().getId(),
                    r.getInquilino().getNombre(),
                    r.getInquilino().getApellidos(),
                    r.getInquilino().getEmail(),
                    r.getInquilino().getTelefono()
            );
        }

        // 3. Devolvemos el DTO gigante
        return new ContratoDetalleResponse(
                r.getId(),
                r.getCodigoVinculacion(),
                r.getApartamento().getNombreInterno(),
                r.getFechaEntrada(),
                r.getFechaSalida(),
                r.getPrecioBaseAlquiler(),
                r.getFianza(),
                r.getEstado(),
                r.getCreadoEn(),
                inquilinoData
        );
    }
}