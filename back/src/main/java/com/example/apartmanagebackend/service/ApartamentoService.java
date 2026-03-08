package com.example.apartmanagebackend.service;

import com.example.apartmanagebackend.domain.Apartamento;
import com.example.apartmanagebackend.domain.Propietario;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoRequest;
import com.example.apartmanagebackend.dto.apartamento.ApartamentoResponse;
import com.example.apartmanagebackend.repository.ApartamentoRepository;
import com.example.apartmanagebackend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

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


    private ApartamentoResponse mapToResponse(Apartamento apartamento) {
        return new ApartamentoResponse(
                apartamento.getId(),
                apartamento.getNombreInterno(),
                apartamento.getDireccion(),
                apartamento.getCiudad(),
                apartamento.getDescripcion(),
                apartamento.getEstado(),
                apartamento.getCreadoEn()
        );
    }

}
