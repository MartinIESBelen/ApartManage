package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.Gasto;
import com.apartmanagebackend.domain.Propietario;
import com.apartmanagebackend.dto.gastos.GastoRequest;
import com.apartmanagebackend.dto.gastos.GastoResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.GastoRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoRepository gastoRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public GastoResponse añadirGasto(Long apartamentoId, GastoRequest request, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        Apartamento apartamento = apartamentoRepository.findByIdAndPropietarioId(apartamentoId, propietario.getId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o acceso denegado"));

        Gasto nuevoGasto = Gasto.builder()
                .apartamento(apartamento)
                .concepto(request.concepto())
                .categoria(request.categoria())
                .tipoGasto(request.tipoGasto())
                .importe(request.importe())
                .fechaGasto(request.fechaGasto())
                .build();

        return mapToResponse(gastoRepository.save(nuevoGasto));
    }

    public List<GastoResponse> obtenerGastosPorApartamento(Long apartamentoId, String emailPropietario) {
        Propietario propietario = (Propietario) usuarioRepository.findByEmail(emailPropietario).orElseThrow();
        apartamentoRepository.findByIdAndPropietarioId(apartamentoId, propietario.getId())
                .orElseThrow(() -> new RuntimeException("Acceso denegado"));

        return gastoRepository.findByApartamentoId(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void eliminarGasto(Long gastoId, String emailPropietario) {
        Gasto gasto = gastoRepository.findById(gastoId).orElseThrow();
        if (!gasto.getApartamento().getPropietario().getEmail().equals(emailPropietario)) {
            throw new RuntimeException("No tienes permisos para borrar este gasto");
        }
        gastoRepository.delete(gasto);
    }

    private GastoResponse mapToResponse(Gasto gasto) {
        return new GastoResponse(
                gasto.getId(),
                gasto.getConcepto(),
                gasto.getCategoria(),
                gasto.getTipoGasto(),
                gasto.getImporte(),
                gasto.getFechaGasto(),
                gasto.getApartamento().getId()
        );
    }

}
