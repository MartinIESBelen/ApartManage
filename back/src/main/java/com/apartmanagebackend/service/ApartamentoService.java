package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.Propietario;
import com.apartmanagebackend.domain.Reserva;
import com.apartmanagebackend.domain.Usuario;
import com.apartmanagebackend.domain.enums.*;
import com.apartmanagebackend.dto.apartamento.ApartamentoRequest;
import com.apartmanagebackend.dto.apartamento.ApartamentoResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ReservaRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- CAMBIADO para soportar readOnly

import java.time.LocalDate; // <-- NUEVO
import java.util.ArrayList; // <-- NUEVO
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    //Crear
    public ApartamentoResponse crearApartamento(ApartamentoRequest request,String emailPropietario){
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Apartamento nuevoApartamento = Apartamento.builder()
                .nombreInterno(request.nombre())
                .direccion(request.direccion())
                .ciudad(request.ciudad())
                .descripcion(request.descripcion())
                .propietario(propietario)
                .build();

        Apartamento guardado = apartamentoRepository.save(nuevoApartamento);
        return mapToResponse(guardado, RelacionVivienda.PROPIETARIO);

    }

    public List<ApartamentoResponse> obtenerMisApartamentos(String emailPropietario){
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow();

        return apartamentoRepository.findByPropietarioId(propietario.getId())
                .stream()
                .map(apto -> mapToResponse(apto, RelacionVivienda.PROPIETARIO))
                .collect(Collectors.toList());
    }

    public ApartamentoResponse obtenerApartamentoPorId(Long id, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Apartamento apartamento = apartamentoRepository.findByIdAndPropietarioId(id, propietario.getId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o acceso denegado"));

        return mapToResponse(apartamento, RelacionVivienda.PROPIETARIO);
    }

    @Transactional(readOnly = true)
    public List<ApartamentoResponse> filtrarMisApartamentos(
            String email,
            String nombre,
            EstadoApartamento estado,
            Boolean conAlertas) {

        // Buscamos al usuario de forma genérica
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Apartamento> misApartamentos;
        RelacionVivienda relacion; // De nuestro Enum

        // Comprobamos su rol real
        if (usuario instanceof Propietario) {
            // Si es casero, le damos sus propiedades
            misApartamentos = apartamentoRepository.findByPropietarioId(usuario.getId());
            relacion = RelacionVivienda.PROPIETARIO;
        } else {
            // Si es inquilino, le damos los pisos donde tiene una reserva
            misApartamentos = reservaRepository.findByInquilinoId(usuario.getId())
                    .stream()
                    .map(Reserva::getApartamento)
                    .distinct()
                    .collect(Collectors.toList());
            relacion = RelacionVivienda.INQUILINO;
        }

        return misApartamentos.stream()
                // Filtro: Por nombre
                .filter(apto -> nombre == null || nombre.isBlank() ||
                        apto.getNombreInterno().toLowerCase().contains(nombre.toLowerCase()))
                // Filtro: Por estado
                .filter(apto -> estado == null || apto.getEstado() == estado)
                // Filtro: Alertas
                .filter(apto -> {
                    if (conAlertas == null || !conAlertas) return true;
                    return !detectarAlertas(apto).isEmpty();
                })
                // ¡MAGIA! Mapeamos usando la relación que descubrimos arriba
                .map(apto -> mapToResponse(apto, relacion))
                .collect(Collectors.toList());
    }

    // EL DETECTIVE DE ALERTAS
    private List<String> detectarAlertas(Apartamento apto) {
        List<String> alertas = new ArrayList<>();

        // Alerta 1: Impagos
        boolean tieneImpagos = apto.getReservas().stream()
                .flatMap(reserva -> reserva.getRecibos().stream())
                .anyMatch(recibo -> recibo.getEstado() == EstadoRecibo.PENDIENTE);
        if (tieneImpagos) alertas.add("Impago detectado: Hay recibos pendientes.");

        // Alerta 2: Fin de contrato inminente
        LocalDate limiteAviso = LocalDate.now().plusDays(30);
        boolean finContrato = apto.getReservas().stream()
                .anyMatch(reserva -> reserva.getFechaSalida().isBefore(limiteAviso)
                        && reserva.getFechaSalida().isAfter(LocalDate.now().minusDays(1)));
        if (finContrato) alertas.add("Atención: Un contrato finaliza en menos de 30 días.");

        // Alerta 3: Desperfectos (inventario)
        boolean tieneDesperfectos = apto.getInventario().stream()
                .anyMatch(item -> item.getEstado() != EstadoItem.BUENO);
        if (tieneDesperfectos) alertas.add("Incidencia: Hay elementos dañados en el inventario.");

        // Dentro de detectarAlertas en ApartamentoService
        boolean tieneIncidenciasAbiertas = apto.getIncidencias().stream()
                .anyMatch(i -> i.getEstado() != EstadoIncidencia.SOLUCIONADA);
        if (tieneIncidenciasAbiertas) alertas.add("Mantenimiento: Hay incidencias pendientes de solución.");

        return alertas;
    }


    private ApartamentoResponse mapToResponse(Apartamento apartamento, RelacionVivienda relacion) {
        return new ApartamentoResponse(
                apartamento.getId(),
                apartamento.getNombreInterno(),
                apartamento.getDireccion(),
                apartamento.getCiudad(),
                apartamento.getDescripcion(),
                apartamento.getEstado(),
                apartamento.getCreadoEn(),
                detectarAlertas(apartamento),
                relacion
        );
    }
}