package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.*;
import com.apartmanagebackend.domain.enums.*;
import com.apartmanagebackend.dto.apartamento.*;
import com.apartmanagebackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApartamentoService {

    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final DocumentoApartamentoRepository documentoRepository;
    private final AlmacenamientoService almacenamiento;

    // ── CRUD Apartamento ──────────────────────────────────────────────────────

    public ApartamentoResponse crearApartamento(ApartamentoRequest request, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Apartamento nuevo = Apartamento.builder()
                .nombreInterno(request.nombre())
                .direccion(request.direccion())
                .ciudad(request.ciudad())
                .descripcion(request.descripcion())
                .propietario(propietario)
                .build();

        Apartamento guardado = apartamentoRepository.save(nuevo);
        almacenamiento.inicializarCarpetasVivienda(guardado);
        return mapToResponse(guardado, RelacionVivienda.PROPIETARIO);
    }

    public List<ApartamentoResponse> obtenerMisApartamentos(String email) {
        Usuario propietario = usuarioRepository.findByEmail(email).orElseThrow();
        return apartamentoRepository.findByPropietarioId(propietario.getId())
                .stream()
                .map(apto -> mapToResponse(apto, RelacionVivienda.PROPIETARIO))
                .collect(Collectors.toList());
    }

    public ApartamentoResponse obtenerApartamento(Long id, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        Apartamento apartamento = apartamentoRepository.findById(id).orElseThrow();

        RelacionVivienda relacion = resolverRelacion(apartamento, usuario);
        return mapToResponse(apartamento, relacion);
    }

    @Transactional(readOnly = true)
    public List<ApartamentoResponse> filtrarMisApartamentos(String email, String nombre,
                                                            EstadoApartamento estado, Boolean conAlertas) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<ApartamentoResponse> todas = new ArrayList<>();

        apartamentoRepository.findByPropietarioId(usuario.getId())
                .forEach(apto -> todas.add(mapToResponse(apto, RelacionVivienda.PROPIETARIO)));

        contratoRepository.findByInquilinoId(usuario.getId()).stream()
                .map(Contrato::getApartamento)
                .distinct()
                .forEach(apto -> todas.add(mapToResponse(apto, RelacionVivienda.INQUILINO)));

        return todas.stream()
                .filter(r -> nombre == null || nombre.isBlank()
                        || r.nombreInterno().toLowerCase().contains(nombre.toLowerCase()))
                .filter(r -> estado == null || r.estado() == estado)
                .filter(r -> conAlertas == null || !conAlertas
                        || (r.alertas() != null && !r.alertas().isEmpty()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApartamentoResponse actualizarApartamento(Long id, ApartamentoRequest request, String emailPropietario) {
        Apartamento apartamento = apartamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        verificarEsPropietario(apartamento, emailPropietario);

        apartamento.setNombreInterno(request.nombre());
        apartamento.setDireccion(request.direccion());
        apartamento.setCiudad(request.ciudad());
        apartamento.setDescripcion(request.descripcion());

        Apartamento actualizado = apartamentoRepository.save(apartamento);

        return mapToResponse(actualizado, RelacionVivienda.PROPIETARIO);
    }

    @Transactional
    public void eliminarApartamento(Long apartamentoId) {
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        almacenamiento.eliminarCarpetaVivienda(apartamento);
        apartamentoRepository.delete(apartamento);
    }

    // ── Imágenes ──────────────────────────────────────────────────────────────

    @Transactional
    public void subirImagen(Long apartamentoId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo está vacío.");

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        String rutaBD = almacenamiento.guardarImagenVivienda(apartamento, file);

        boolean esLaPrimera = apartamento.getImagenes().isEmpty();
        ImagenesApartamento imagen = ImagenesApartamento.builder()
                .apartamento(apartamento)
                .rutaArchivo(rutaBD)
                .esPrincipal(esLaPrimera)
                .orden(apartamento.getImagenes().size() + 1)
                .build();

        apartamento.getImagenes().add(imagen);
        apartamentoRepository.save(apartamento);
    }

    // ── Documentos ────────────────────────────────────────────────────────────

    public List<DocumentoResponse> obtenerDocumentos(Long apartamentoId, String email) {
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        verificarAcceso(apartamento, email);

        return documentoRepository.findByApartamentoId(apartamentoId).stream()
                .map(doc -> new DocumentoResponse(
                        doc.getId(), doc.getRutaArchivo(),
                        doc.getNombreOriginal(), doc.getFechaSubida()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void subirDocumento(Long apartamentoId, MultipartFile file, String email) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo está vacío.");

        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        verificarEsPropietario(apartamento, email);

        String rutaBD = almacenamiento.guardarDocumentoVivienda(apartamento, file);

        documentoRepository.save(DocumentoApartamento.builder()
                .apartamento(apartamento)
                .rutaArchivo(rutaBD)
                .nombreOriginal(file.getOriginalFilename())
                .fechaSubida(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void borrarDocumento(Long apartamentoId, Long docId, String email) throws IOException {
        Apartamento apartamento = apartamentoRepository.findById(apartamentoId)
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado"));

        verificarEsPropietario(apartamento, email);

        DocumentoApartamento documento = documentoRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));

        almacenamiento.eliminarArchivo(documento.getRutaArchivo());
        documentoRepository.delete(documento);
    }

    // ── Helpers de seguridad ──────────────────────────────────────────────────

    private RelacionVivienda resolverRelacion(Apartamento apartamento, Usuario usuario) {
        if (apartamento.getPropietario().getId().equals(usuario.getId())) {
            return RelacionVivienda.PROPIETARIO;
        }
        if (contratoRepository.existsByApartamentoIdAndInquilinoIdAndEstado(
                apartamento.getId(), usuario.getId(), EstadoContrato.CONFIRMADA)) {
            return RelacionVivienda.INQUILINO;
        }
        throw new RuntimeException("No tienes permisos para ver este apartamento");
    }

    private void verificarAcceso(Apartamento apartamento, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        resolverRelacion(apartamento, usuario); // lanza excepción si no tiene acceso
    }

    private void verificarEsPropietario(Apartamento apartamento, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        if (!apartamento.getPropietario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Sin permisos para realizar esta acción");
        }
    }

    // ── Alertas ───────────────────────────────────────────────────────────────

    private List<String> detectarAlertas(Apartamento apto) {
        List<String> alertas = new ArrayList<>();

        boolean tieneImpagos = apto.getTransacciones().stream()
                .anyMatch(t -> t.getTipo() == TipoTransaccion.INGRESO &&
                        (t.getEstado() == EstadoTransaccion.PENDIENTE
                                || t.getEstado() == EstadoTransaccion.VENCIDO));
        if (tieneImpagos) alertas.add("Impago detectado: Hay cobros pendientes o vencidos.");

        LocalDate limite = LocalDate.now().plusDays(30);
        boolean finContrato = apto.getContratos().stream()
                .anyMatch(c -> c.getFechaSalida().isBefore(limite)
                        && c.getFechaSalida().isAfter(LocalDate.now().minusDays(1)));
        if (finContrato) alertas.add("Atención: Un contrato finaliza en menos de 30 días.");

        boolean tieneDesperfectos = apto.getInventario().stream()
                .anyMatch(item -> item.getEstado() != EstadoItem.BUENO);
        if (tieneDesperfectos) alertas.add("Incidencia: Hay elementos dañados en el inventario.");

        boolean tieneIncidencias = apto.getIncidencias().stream()
                .anyMatch(i -> i.getEstado() != EstadoIncidencia.SOLUCIONADA);
        if (tieneIncidencias) alertas.add("Mantenimiento: Hay incidencias pendientes de solución.");

        return alertas;
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private ApartamentoResponse mapToResponse(Apartamento apartamento, RelacionVivienda relacion) {
        String nombreInquilino = null;
        Long idReservaActiva = null;

        if (apartamento.getContratos() != null) {
            var reservaOpt = apartamento.getContratos().stream()
                    .filter(r -> r.getEstado() == EstadoContrato.CONFIRMADA)
                    .findFirst();

            if (reservaOpt.isPresent()) {
                var reserva = reservaOpt.get();
                nombreInquilino = reserva.getInquilino().getNombre() + " "
                        + reserva.getInquilino().getApellidos();
                idReservaActiva = reserva.getId();
            }
        }

        String imagenPrincipal = null;
        if (apartamento.getImagenes() != null && !apartamento.getImagenes().isEmpty()) {
            imagenPrincipal = apartamento.getImagenes().stream()
                    .filter(ImagenesApartamento::isEsPrincipal)
                    .map(ImagenesApartamento::getRutaArchivo)
                    .findFirst()
                    .orElse(apartamento.getImagenes().get(0).getRutaArchivo());
        }

        return new ApartamentoResponse(
                apartamento.getId(), apartamento.getNombreInterno(),
                apartamento.getDireccion(), apartamento.getCiudad(),
                apartamento.getDescripcion(), apartamento.getEstado(),
                apartamento.getCreadoEn(), detectarAlertas(apartamento),
                relacion, nombreInquilino, idReservaActiva, imagenPrincipal
        );
    }
}