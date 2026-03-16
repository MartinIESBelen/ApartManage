package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Apartamento;
import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.domain.enums.EstadoApartamento;
import com.example.apartmanagebackend.domain.enums.EstadoIncidencia;
import com.example.apartmanagebackend.domain.enums.EstadoItem; // <-- NUEVO
import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoRequest;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoResponse;
import com.example.apartmanagebackend.repository.ApartamentoRepository;
import com.example.apartmanagebackend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- CAMBIADO para soportar readOnly

import java.time.LocalDate; // <-- NUEVO
import java.util.ArrayList; // <-- NUEVO
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartamentoService {

    private ApartamentoRepository apartamentoRepository;
    private UsuarioRepository usuarioRepository;

    public ApartamentoService(ApartamentoRepository apartamentoRepository, UsuarioRepository usuarioRepository) {
        this.apartamentoRepository = apartamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

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
        return mapToResponse(guardado);

    }

    public List<ApartamentoResponse> obtenerMisApartamentos(String emailPropietario){
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow();

        return apartamentoRepository.findByPropietarioId(propietario.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApartamentoResponse obtenerApartamentoPorId(Long id, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Apartamento apartamento = apartamentoRepository.findByIdAndPropietarioId(id, propietario.getId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o acceso denegado"));

        return mapToResponse(apartamento);
    }

    // MÉTODO DE FILTRADO CON ALERTAS
    @Transactional(readOnly = true)
    public List<ApartamentoResponse> filtrarMisApartamentos(
            String emailPropietario,
            String nombre,
            EstadoApartamento estado,
            Boolean conAlertas) { // <-- Cambiado de pagado a conAlertas

        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        List<Apartamento> misApartamentos = apartamentoRepository.findByPropietarioId(propietario.getId());

        return misApartamentos.stream()
                // Filtro 1: Por nombre
                .filter(apto -> nombre == null || nombre.isBlank() ||
                        apto.getNombreInterno().toLowerCase().contains(nombre.toLowerCase()))

                // Filtro 2: Por estado (ACTIVO / INACTIVO)
                .filter(apto -> estado == null || apto.getEstado() == estado)

                // Filtro 3: Solo pisos con alertas (si marcan la casilla)
                .filter(apto -> {
                    if (conAlertas == null || !conAlertas) return true;
                    return !detectarAlertas(apto).isEmpty(); // Pasa si hay al menos 1 alerta
                })
                .map(this::mapToResponse) // Convertimos a DTO (esto ya incluye las alertas)
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
        boolean tieneIncidenciasAbiertas = apto.getIncidencias().stream() // Necesitas añadir @OneToMany en Apartamento.java
                .anyMatch(i -> i.getEstado() != EstadoIncidencia.SOLUCIONADA);
        if (tieneIncidenciasAbiertas) alertas.add("Mantenimiento: Hay incidencias pendientes de solución.");

        return alertas;
    }


    private ApartamentoResponse mapToResponse(Apartamento apartamento) {
        return new ApartamentoResponse(
                apartamento.getId(),
                apartamento.getNombreInterno(),
                apartamento.getDireccion(),
                apartamento.getCiudad(),
                apartamento.getDescripcion(),
                apartamento.getEstado(),
                apartamento.getCreadoEn(),
                detectarAlertas(apartamento) // <-- Metemos las alertas aquí
        );
    }
}