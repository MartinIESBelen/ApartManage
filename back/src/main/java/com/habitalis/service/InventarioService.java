package com.habitalis.service;

import com.habitalis.domain.Apartamento;
import com.habitalis.domain.ElementoInventario;
import com.habitalis.domain.Usuario; // <-- Importante: Añadimos Usuario
import com.habitalis.domain.enums.EstadoContrato;
import com.habitalis.dto.inventario.InventarioRequest;
import com.habitalis.dto.inventario.InventarioResponse;
import com.habitalis.repository.ApartamentoRepository;
import com.habitalis.repository.ElementoInventarioRepository;
import com.habitalis.repository.ContratoRepository;
import com.habitalis.repository.UsuarioRepository;
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
    private final ContratoRepository contratoRepository;

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

    public List<InventarioResponse> listarInventarioPorApartamento(Long apartamentoId, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        boolean esPropietario = apartamento.getPropietario().getId().equals(usuario.getId());

        boolean esInquilino = contratoRepository.existsByApartamentoIdAndInquilinoIdAndEstado(
                apartamentoId, usuario.getId(), EstadoContrato.CONFIRMADA);

        if (!esPropietario && !esInquilino) {
            throw new RuntimeException("No tienes permisos para ver este inventario");
        }

        return inventarioRepository.findByApartamentoId(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

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

    public InventarioResponse editarItem(Long apartamentoId, Long itemId, InventarioRequest request, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ElementoInventario item = inventarioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        if (!item.getApartamento().getId().equals(apartamentoId) ||
                !item.getApartamento().getPropietario().getId().equals(propietario.getId())) {
            throw new RuntimeException("No tienes permisos para editar este elemento");
        }

        item.setNombre(request.nombre());
        item.setCategoria(request.categoria());
        item.setEstado(request.estado());
        item.setPrecioCompra(request.precioCompra());
        item.setFechaCompra(request.fechaCompra());

        return mapToResponse(inventarioRepository.save(item));
    }

    public InventarioResponse marcarComoRoto(Long apartamentoId, Long itemId, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ElementoInventario item = inventarioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        if (!item.getApartamento().getId().equals(apartamentoId)) {
            throw new RuntimeException("El elemento no coincide con el apartamento");
        }

        boolean esPropietario = item.getApartamento().getPropietario().getId().equals(usuario.getId());

        boolean esInquilino = contratoRepository.existsByApartamentoIdAndInquilinoIdAndEstado(
                apartamentoId, usuario.getId(), EstadoContrato.CONFIRMADA);

        if (!esPropietario && !esInquilino) {
            throw new RuntimeException("No tienes permisos para reportar roturas en este apartamento");
        }

        item.setEstado(com.habitalis.domain.enums.EstadoItem.ROTO);

        return mapToResponse(inventarioRepository.save(item));
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