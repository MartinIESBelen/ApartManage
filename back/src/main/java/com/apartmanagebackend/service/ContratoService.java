package com.apartmanagebackend.service;

import com.apartmanagebackend.domain.*;
import com.apartmanagebackend.domain.enums.EstadoContrato;
import com.apartmanagebackend.dto.contrato.*;
import com.apartmanagebackend.repository.ApartamentoRepository;
import com.apartmanagebackend.repository.ContratoRepository; // Asumo que aún no has renombrado el repositorio
import com.apartmanagebackend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.almacenamiento.raiz}")
    private String rutaRaiz;

    private String getCarpetaVivienda(Apartamento apartamento) {
        Usuario prop = apartamento.getPropietario();
        String propNombre = prop.getNombre().replaceAll("[^a-zA-Z0-9]", "_");
        String aptoNombre = apartamento.getNombreInterno().replaceAll("[^a-zA-Z0-9]", "_");
        return "Usuarios/" + propNombre + "-" + prop.getId() + "/Viviendas/" + aptoNombre + "-" + apartamento.getId() + "/";
    }

    @Transactional
    public ContratoResponse crearContrato(Long apartamentoId, ContratoRequest request, String emailPropietario) {
        validarFechas(request.fechaEntrada(), request.fechaSalida());

        Apartamento apartamento = obtenerApartamentoVerificandoPropietario(apartamentoId, emailPropietario);
        String codigoGenerado = "APT-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Contrato nuevoContrato = Contrato.builder()
                .apartamento(apartamento)
                .codigoVinculacion(codigoGenerado)
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .precioBaseAlquiler(request.precioBaseAlquiler())
                .fianza(request.fianza())
                .estado(EstadoContrato.PENDIENTE)
                .build();

        return mapToResponse(contratoRepository.save(nuevoContrato));
    }

    @Transactional
    public ContratoResponse crearContratoManual(Long apartamentoId, ContratoManualRequest request, String emailPropietario) {
        validarFechas(request.fechaEntrada(), request.fechaSalida());

        Apartamento apartamento = obtenerApartamentoVerificandoPropietario(apartamentoId, emailPropietario);
        Usuario inquilino = obtenerOCrearInquilinoFantasma(request);
        String codigoInterno = "MANUAL-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        Contrato nuevoContrato = Contrato.builder()
                .apartamento(apartamento)
                .inquilino(inquilino)
                .codigoVinculacion(codigoInterno)
                .fechaEntrada(request.fechaEntrada())
                .fechaSalida(request.fechaSalida())
                .precioBaseAlquiler(request.precioBaseAlquiler())
                .estado(EstadoContrato.CONFIRMADA)
                .build();

        return mapToResponse(contratoRepository.save(nuevoContrato));
    }

    @Transactional
    public ContratoResponse vincularInquilino(VincularRequest request, String emailInquilino) {
        Usuario inquilino = usuarioRepository.findByEmail(emailInquilino)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Contrato contrato = contratoRepository.findByCodigoVinculacion(request.codigoVinculacion())
                .orElseThrow(() -> new RuntimeException("Código de vinculación inválido o no existe"));

        if (contrato.getEstado() != EstadoContrato.PENDIENTE) {
            throw new RuntimeException("Este código ya ha sido usado o el contrato no está disponible");
        }

        contrato.setInquilino(inquilino);
        contrato.setEstado(EstadoContrato.CONFIRMADA);

        return mapToResponse(contratoRepository.save(contrato));
    }

    public List<ContratoResponse> listarContratosPorApartamento(Long apartamentoId, String emailPropietario) {
        obtenerApartamentoVerificandoPropietario(apartamentoId, emailPropietario); // Solo para validar permisos

        return contratoRepository.findByApartamentoId(apartamentoId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<ContratoResponse> obtenerMisContratosPropietario(String emailPropietario) {
        return contratoRepository.findMisContratosComoPropietario(emailPropietario)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ContratoDetalleResponse obtenerDetalleContrato(Long contratoId, String emailPropietario) {
        Contrato r = contratoRepository.findByIdAndPropietarioEmail(contratoId, emailPropietario)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado o no tienes permisos"));

        return mapToDetalleResponse(r);
    }

    @Transactional
    public void subirContratoPdf(Long contratoId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("El archivo PDF está vacío.");

        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        String rutaRelativaVivienda = getCarpetaVivienda(contrato.getApartamento());
        String carpetaDestinoFisica = rutaRaiz + rutaRelativaVivienda + "docs/";
        Files.createDirectories(Paths.get(carpetaDestinoFisica));

        String nombreArchivoUnico = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rutaFinalFisica = Paths.get(carpetaDestinoFisica + nombreArchivoUnico);
        Files.copy(file.getInputStream(), rutaFinalFisica);

        contrato.setContratoPdf(rutaRelativaVivienda + "docs/" + nombreArchivoUnico);
        contratoRepository.save(contrato);
    }

    private void validarFechas(java.time.LocalDate entrada, java.time.LocalDate salida) {
        if (salida.isBefore(entrada) || salida.isEqual(entrada)) {
            throw new RuntimeException("La fecha de salida debe ser estrictamente posterior a la de entrada.");
        }
    }

    private Apartamento obtenerApartamentoVerificandoPropietario(Long apartamentoId, String emailPropietario) {
        Usuario propietario = usuarioRepository.findByEmail(emailPropietario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return apartamentoRepository.findById(apartamentoId)
                .filter(apt -> apt.getPropietario().getId().equals(propietario.getId()))
                .orElseThrow(() -> new RuntimeException("Apartamento no encontrado o sin permisos"));
    }

    private Usuario obtenerOCrearInquilinoFantasma(ContratoManualRequest request) {
        return usuarioRepository.findByEmail(request.emailInquilino()).orElseGet(() -> {
            Usuario nuevoInquilino = new Usuario();
            nuevoInquilino.setNombre(request.nombreInquilino());
            nuevoInquilino.setApellidos(request.apellidosInquilino());
            nuevoInquilino.setEmail(request.emailInquilino());
            nuevoInquilino.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            nuevoInquilino.setTelefono(request.telefonoInquilino());
            nuevoInquilino.setDniPasaporte(request.dniInquilino());
            nuevoInquilino.setFechaNacimiento(request.fechaNacimientoInquilino());
            nuevoInquilino.setRol(com.apartmanagebackend.domain.enums.RolUsuario.INQUILINO);
            return usuarioRepository.save(nuevoInquilino);
        });
    }

    private ContratoResponse mapToResponse(Contrato contrato) {
        return new ContratoResponse(
                contrato.getId(),
                contrato.getCodigoVinculacion(),
                contrato.getFechaEntrada(),
                contrato.getFechaSalida(),
                contrato.getPrecioBaseAlquiler(),
                contrato.getEstado(),
                contrato.getApartamento().getNombreInterno(),
                contrato.getInquilino() != null ? contrato.getInquilino().getNombre() + " " + contrato.getInquilino().getApellidos() : "Sin asignar"
        );
    }

    private ContratoDetalleResponse mapToDetalleResponse(Contrato r) {
        ContratoDetalleResponse.InquilinoPublico inquilinoData = null;
        if (r.getInquilino() != null) {
            inquilinoData = new ContratoDetalleResponse.InquilinoPublico(
                    r.getInquilino().getId(), r.getInquilino().getNombre(), r.getInquilino().getApellidos(),
                    r.getInquilino().getEmail(), r.getInquilino().getTelefono()
            );
        }
        return new ContratoDetalleResponse(
                r.getId(), r.getCodigoVinculacion(), r.getApartamento().getNombreInterno(),
                r.getFechaEntrada(), r.getFechaSalida(), r.getPrecioBaseAlquiler(),
                r.getFianza(), r.getEstado(), r.getCreadoEn(), inquilinoData
        );
    }
}