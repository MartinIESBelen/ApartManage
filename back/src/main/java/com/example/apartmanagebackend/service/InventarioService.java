package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Apartamento;
import com.example.apartmanagebackend.domain.ElementoInventario;
import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.dto.inventario.InventarioRequest;
import com.example.apartmanagebackend.dto.inventario.InventarioResponse;
import com.example.apartmanagebackend.repository.ApartamentoRepository;
import com.example.apartmanagebackend.repository.ElementoInventarioRepository;
import com.example.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final ElementoInventarioRepository inventarioRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public InventarioResponse agregarItem(Long apartamentoId, InventarioRequest request, String emailPropietario) {
        // 1. Obtener al propietario logueado
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        // 2. Buscar el apartamento Y verificar que pertenece a este propietario (Seguridad)
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o no tienes permisos"));

        // 3. Crear el elemento de inventario
        ElementoInventario nuevoItem = ElementoInventario.builder()
                .apartamento(apartamento)
                .nombre(request.nombre())
                .categoria(request.categoria())
                .estado(request.estado())
                .precioCompra(request.precioCompra())
                .fechaCompra(request.fechaCompra())
                .build();

        // 4. Guardar y mapear a DTO
        ElementoInventario guardado = inventarioRepository.save(nuevoItem);
        return mapToResponse(guardado);
    }

    public List<InventarioResponse> listarInventarioPorApartamento(Long apartamentoId, String emailPropietario) {
        // Misma validación de seguridad: ¿Es tu piso?
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario).orElseThrow();

        boolean esSuPiso = apartamentoRepository.findById(apartamentoId)
                .map(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElse(false);

        if (!esSuPiso) {
            throw new RuntimeException("No tienes permisos para ver este inventario");
        }

        return inventarioRepository.findByApartamentoId(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventarioResponse mapToResponse(ElementoInventario item) {
        return new InventarioResponse(
                item.getId(),
                item.getNombre(),
                item.getCategoria(),
                item.getEstado(),
                item.getPrecioCompra(),
                item.getFechaCompra()
        );
    }
}
