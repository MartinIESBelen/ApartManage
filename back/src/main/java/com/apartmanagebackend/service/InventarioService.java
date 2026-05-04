package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.ElementoInventario;
import com.apartmanagebackend.domain.Usuario; // <-- Importante: Añadimos Usuario
import com.apartmanagebackend.dto.inventario.InventarioRequest;
import com.apartmanagebackend.dto.inventario.InventarioResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ElementoInventarioRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
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

    //CREAR UN ELEMENTO
    public InventarioResponse agregarItem(Long apartamentoId, InventarioRequest request, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o no tienes permisos"));

        ElementoInventario nuevoItem = ElementoInventario.builder()
                .apartamento(apartamento)
                .nombre(request.nombre())
                .categoria(request.categoria())
                .estado(request.estado())
                .precioCompra(request.precioCompra())
                .fechaCompra(request.fechaCompra())
                .build();

        ElementoInventario guardado = inventarioRepository.save(nuevoItem);
        return mapToResponse(guardado);
    }

    public List<InventarioResponse> listarInventarioPorApartamento(Long apartamentoId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();

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

    // ELIMINAR UN ELEMENTO
    public void eliminarItem(Long apartamentoId, Long itemId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ElementoInventario item = inventarioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        if (!item.getApartamento().getId().equals(apartamentoId) ||
                !item.getApartamento().getPropietario().getId().equals(propietario.getId())) {
            throw new RuntimeException("No tienes permisos para borrar este elemento");
        }

        inventarioRepository.delete(item);
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