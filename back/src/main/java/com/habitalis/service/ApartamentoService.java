package com.habitalis.service;

import com.habitalis.domain.*;
import com.habitalis.domain.enums.*;
import com.habitalis.dto.apartamento.*;
import com.habitalis.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final AlertaService alertaService;


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

        Set<ApartamentoResponse> todas = new LinkedHashSet<>();

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

    private Contrato buscarContratoConfirmado(Apartamento apartamento) {
        if (apartamento.getContratos() == null) return null;

        return apartamento.getContratos().stream()
                .filter(c -> c.getEstado() == EstadoContrato.CONFIRMADA)
                .findFirst()
                .orElse(null);
    }

    private String obtenerRutaImagenPrincipal(Apartamento apartamento) {
        if (apartamento.getImagenes() == null || apartamento.getImagenes().isEmpty()) {
            return null;
        }

        return apartamento.getImagenes().stream()
                .filter(ImagenesApartamento::isEsPrincipal)
                .map(ImagenesApartamento::getRutaArchivo)
                .findFirst()
                .orElse(apartamento.getImagenes().get(0).getRutaArchivo());
    }


    private ApartamentoResponse mapToResponse(Apartamento apartamento, RelacionVivienda relacion) {

        Contrato contratoActivo = buscarContratoConfirmado(apartamento);

        String nombreInquilino = (contratoActivo != null) ?
                contratoActivo.getInquilino().getNombre() + " " + contratoActivo.getInquilino().getApellidos() : null;

        Long idReservaActiva = (contratoActivo != null) ? contratoActivo.getId() : null;

        EstadoApartamento estadoCalculado = (contratoActivo != null) ?
                EstadoApartamento.ACTIVO : EstadoApartamento.INACTIVO;

        Set<TipoAlerta> alertasActivas = alertaService.evaluarAlertasApartamento(apartamento.getId());
        String imagenPrincipal = obtenerRutaImagenPrincipal(apartamento);

        return new ApartamentoResponse(
                apartamento.getId(),
                apartamento.getNombreInterno(),
                apartamento.getDireccion(),
                apartamento.getCiudad(),
                apartamento.getDescripcion(),
                estadoCalculado,
                apartamento.getCreadoEn(),
                alertasActivas,
                relacion,
                nombreInquilino,
                idReservaActiva,
                imagenPrincipal
        );
    }


}