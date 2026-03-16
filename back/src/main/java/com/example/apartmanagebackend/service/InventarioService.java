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

    //CREAR UN ELEMENTO
    public InventarioResponse agregarItem(Long apartamentoId, InventarioRequest request, String emailPropietario) {
        //  Obtener al propietario logueado
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        //  Buscar el apartamento Y verificar que pertenece a este propietario (Seguridad)
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o no tienes permisos"));

        //  Crear el elemento de inventario
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
        // Volvemos validar el piso (comprobar)
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

    // ELIMINAR UN ELEMENTO
    public void eliminarItem(Long apartamentoId, Long itemId, String emailPropietario) {
        //  Validamos al propietario
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        //  Buscamos el item
        ElementoInventario item = inventarioRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Elemento no encontrado"));

        //  Comprobar si el item pertenece al apartamento
        if (!item.getApartamento().getId().equals(apartamentoId) ||
                !item.getApartamento().getPropietario().getId().equals(propietario.getId())) {
            throw new RuntimeException("No tienes permisos para borrar este elemento");
        }

        // Borramos
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
