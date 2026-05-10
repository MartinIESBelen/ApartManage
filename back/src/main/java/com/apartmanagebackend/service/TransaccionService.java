package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.Apartamento;
import com.apartmanagebackend.domain.Contrato;
import com.apartmanagebackend.domain.Transaccion;
import com.apartmanagebackend.domain.Usuario;
import com.apartmanagebackend.domain.enums.EstadoContrato;
import com.apartmanagebackend.dto.transaccion.TransaccionRequest;
import com.apartmanagebackend.dto.transaccion.TransaccionResponse;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ContratoRepository;
import com.apartmanagebackend.repository.TransaccionRepository;
import com.apartmanagebackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ContratoRepository contratoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public List<TransaccionResponse> crearTransaccion(TransaccionRequest request, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();
        Apartamento apartamento = apartamentoRepository.findByIdAndPropietarioId(request.apartamentoId(), propietario.getId())
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));

        List<Transaccion> transaccionesGuardadas = new ArrayList<>();

        if (request.dividirEntreTodos()) {
            List<Contrato> contratosActivos = contratoRepository.findAll().stream()
                    .filter(r -> r.getApartamento().getId().equals(apartamento.getId()))
                    .filter(r -> r.getEstado() == EstadoContrato.CONFIRMADA)
                    .toList();

            if (contratosActivos.isEmpty()) {
                throw new RuntimeException("No hay inquilinos activos en este piso para dividir la transacción.");
            }

            // Dividir importe equitativamente (Redondeo a 2 decimales para evitar fallos contables)
            BigDecimal importeDividido = request.importe().divide(new BigDecimal(contratosActivos.size()), 2, RoundingMode.HALF_UP);

            for (Contrato res : contratosActivos) {
                Transaccion t = construirTransaccionBase(apartamento, res, request, importeDividido);
                transaccionesGuardadas.add(transaccionRepository.save(t));
            }
        }
        // CASO B: ASIGNAR A UN INQUILINO CONCRETO O GASTO GENERAL DEL PISO
        else {
            Contrato contrato = null;
            if (request.contratoId() != null) {
                contrato = contratoRepository.findById(request.contratoId()).orElseThrow();
            }
            Transaccion t = construirTransaccionBase(apartamento, contrato, request, request.importe());
            transaccionesGuardadas.add(transaccionRepository.save(t));
        }

        return transaccionesGuardadas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public TransaccionResponse actualizarTransaccion(Long id, TransaccionRequest request, String emailPropietario) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();
        if (!transaccion.getApartamento().getPropietario().getId().equals(propietario.getId())) {
            throw new RuntimeException("No tienes permisos para editar esta transacción");
        }

        transaccion.setTipo(request.tipo());
        transaccion.setCategoria(request.categoria());
        transaccion.setEstado(request.estado());
        transaccion.setConcepto(request.concepto());
        transaccion.setImporte(request.importe());
        transaccion.setFechaEmision(request.fechaEmision());

        return mapToResponse(transaccionRepository.save(transaccion));
    }

    @Transactional
    public void eliminarTransaccion(Long id, String emailPropietario) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        Usuario propietario = usuarioRepository.findByEmail(emailPropietario).orElseThrow();
        if (!transaccion.getApartamento().getPropietario().getId().equals(propietario.getId())) {
            throw new RuntimeException("No tienes permisos para eliminar esta transacción");
        }
        transaccionRepository.delete(transaccion);
    }

    public List<TransaccionResponse> obtenerPorApartamento(Long apartamentoId) {
        return transaccionRepository.findByApartamentoIdOrderByFechaEmisionDesc(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> obtenerPorContrato(Long contratoId) {
        return transaccionRepository.findByContratoIdOrderByFechaEmisionDesc(contratoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransaccionResponse> obtenerFiltradas(Long apartamentoId, String periodo, String emailPropietario) {

        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = LocalDate.of(2000, 1, 1);
        LocalDate fechaFin = LocalDate.of(2100, 12, 31);

        if (periodo != null) {
            switch (periodo) {
                case "MES":
                    fechaInicio = hoy.minusDays(30);
                    break;
                case "TRIMESTRE":
                    fechaInicio = hoy.minusMonths(3);
                    break;
                case "ANIO_ACTUAL":
                    fechaInicio = hoy.withDayOfYear(1);
                    break;
                case "SIEMPRE":
                default:
                    break;
            }
        }

        List<Transaccion> transaccionesCrudas = transaccionRepository.buscarFiltradasSeguras(
                apartamentoId,
                emailPropietario,
                fechaInicio,
                fechaFin
        );

        return transaccionesCrudas.stream()
                .map(this::mapToResponse) // Asumo que ya tienes este método de mapeo creado de antes
                .collect(Collectors.toList());
    }

    private Transaccion construirTransaccionBase(Apartamento apto, Contrato contrato, TransaccionRequest req, BigDecimal importe) {
        return Transaccion.builder()
                .apartamento(apto)
                .contrato(contrato)
                .tipo(req.tipo())
                .categoria(req.categoria())
                .estado(req.estado())
                .concepto(req.concepto())
                .importe(importe)
                .comentario(req.comentario())
                .fechaEmision(req.fechaEmision() != null ? req.fechaEmision() : java.time.LocalDate.now())
                .fechaVencimiento(req.fechaVencimiento())
                .build();
    }

    private TransaccionResponse mapToResponse(Transaccion t) {

        String nombreApto = t.getApartamento() != null ? t.getApartamento().getNombreInterno() : null;

        String nombreInq = null;
        if (t.getContrato() != null && t.getContrato().getInquilino() != null) {
            nombreInq = t.getContrato().getInquilino().getNombre() + " " + t.getContrato().getInquilino().getApellidos();
        }

        return new TransaccionResponse(
                t.getId(),
                t.getApartamento() != null ? t.getApartamento().getId() : null,
                nombreApto,
                t.getContrato() != null ? t.getContrato().getId() : null,
                nombreInq,
                t.getTipo(),
                t.getCategoria(),
                t.getEstado(),
                t.getConcepto(),
                t.getImporte(),
                t.getComentario(),
                t.getFechaEmision(),
                t.getFechaVencimiento(),
                t.getFechaPago()
        );
    }
}