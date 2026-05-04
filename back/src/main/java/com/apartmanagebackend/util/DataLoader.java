package com.apartmanagebackend.util;

import com.apartmanagebackend.domain.*;
import com.apartmanagebackend.domain.enums.*;
import com.apartmanagebackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    // AHORA SOLO USAMOS EL USUARIO REPOSITORY
    private final UsuarioRepository usuarioRepository;
    private final ApartamentoRepository apartamentoRepository;
    private final ElementoInventarioRepository inventarioRepository;
    private final ReservaRepository reservaRepository;
    private final TransaccionRepository transaccionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando la carga de datos de prueba para ApartManage...");

        // Evitar duplicados: si ya hay usuarios, no hacemos nada
        if (usuarioRepository.count() > 0) {
            log.info("La base de datos ya contiene información. Saltando DataLoader.");
            return;
        }

        // Crear USUARIO 1 (Hará de Propietario)
        Usuario propietario = Usuario.builder()
                .nombre("Martin")
                .apellidos("Sierra")
                .email("propietario@email.com")
                .password(passwordEncoder.encode("123456"))
                .telefono("600123456")
                .dniPasaporte("12345678A")
                .rol(RolUsuario.PROPIETARIO)
                .iban("ES1234567890123456789012")
                .direccionFiscal("Calle Falsa 123, Madrid")
                .build();
        usuarioRepository.save(propietario);

        // Crear USUARIO 2 (Hará de Propietario)
        Usuario propietario2 = Usuario.builder()
                .nombre("Martin2")
                .apellidos("Sierra")
                .email("propietario2@email.com")
                .password(passwordEncoder.encode("1234567"))
                .telefono("600143455")
                .dniPasaporte("12345678F")
                .rol(RolUsuario.PROPIETARIO)
                .iban("ES1234567890123456789013")
                .direccionFiscal("Calle Falsa 123, Malaga")
                .build();
        usuarioRepository.save(propietario2);

        // Crear USUARIO 3 (Hará de Inquilino)
        Usuario inquilino = Usuario.builder()
                .nombre("Martin")
                .apellidos("Godinez")
                .email("inquilino@email.com")
                .password(passwordEncoder.encode("123456"))
                .telefono("600654321")
                .dniPasaporte("87654321B")
                .rol(RolUsuario.INQUILINO)
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .direccionHabitual("Av. Siempre Viva 742, Barcelona")
                .build();
        usuarioRepository.save(inquilino);

        // Crear APARTAMENTO
        Apartamento apartamento = Apartamento.builder()
                .propietario(propietario)
                .nombreInterno("Ático Sol")
                .direccion("Plaza Mayor 1, 5º Derecha")
                .ciudad("Madrid")
                .descripcion("Precioso ático muy luminoso en el centro.")
                .estado(EstadoApartamento.ACTIVO)
                .build();
        apartamentoRepository.save(apartamento);

        // Crear ELEMENTOS DE INVENTARIO
        ElementoInventario sofa = ElementoInventario.builder()
                .apartamento(apartamento)
                .nombre("Sofá de 3 plazas (Ikea)")
                .categoria(CategoriaItem.MUEBLE)
                .estado(EstadoItem.BUENO)
                .precioCompra(new BigDecimal("350.00"))
                .fechaCompra(LocalDate.of(2023, 1, 10))
                .build();
        inventarioRepository.save(sofa);

        ElementoInventario tv = ElementoInventario.builder()
                .apartamento(apartamento)
                .nombre("Smart TV Samsung 55'")
                .categoria(CategoriaItem.ELECTRODOMESTICO)
                .estado(EstadoItem.NUEVO)
                .precioCompra(new BigDecimal("499.99"))
                .fechaCompra(LocalDate.of(2024, 2, 20))
                .build();
        inventarioRepository.save(tv);

        // Crear RESERVA (Actual)
        Reserva reserva = Reserva.builder()
                .apartamento(apartamento)
                .inquilino(inquilino)
                .codigoVinculacion("RES-2026-001")
                .fechaEntrada(LocalDate.of(2026, 1, 1))
                .fechaSalida(LocalDate.of(2027, 1, 1))
                .precioBaseAlquiler(new BigDecimal("1200.00"))
                .estado(EstadoReserva.CONFIRMADA)
                .build();
        reservaRepository.save(reserva);

        // --- INICIO DE CARGA DE TRANSACCIONES ---

        // Enero: Alquiler 1200, Gasto 250
        crearMesCompleto(reserva, 1, 2026, new BigDecimal("1200"), new BigDecimal("250"), "Revisión Caldera", CategoriaTransaccion.MANTENIMIENTO_RUTINARIO);

        // Febrero: Alquiler 1200, Gasto 100
        crearMesCompleto(reserva, 2, 2026, new BigDecimal("1200"), new BigDecimal("100"), "Limpieza profunda", CategoriaTransaccion.LIMPIEZA);

        // Marzo: Alquiler 1200, Gasto 450 (Reparación cara)
        crearMesCompleto(reserva, 3, 2026, new BigDecimal("1200"), new BigDecimal("450"), "Reparación Termo Eléctrico", CategoriaTransaccion.REPARACION_INCIDENCIA);

        // Abril: Alquiler 1250 (Simulamos una subida), Gasto 100
        crearMesCompleto(reserva, 4, 2026, new BigDecimal("1250"), new BigDecimal("100"), "Cuota Comunidad", CategoriaTransaccion.COMUNIDAD);

        // Mayo: Alquiler PENDIENTE
        Transaccion alquilerMayo = Transaccion.builder()
                .apartamento(apartamento)
                .reserva(reserva)
                .tipo(TipoTransaccion.INGRESO)
                .categoria(CategoriaTransaccion.ALQUILER)
                .estado(EstadoTransaccion.PENDIENTE)
                .concepto("Alquiler Mayo 2026")
                .importe(new BigDecimal("1250.00"))
                .fechaEmision(LocalDate.of(2026, 5, 1))
                .fechaVencimiento(LocalDate.of(2026, 5, 5))
                .build();
        transaccionRepository.save(alquilerMayo);

        // Suministros separados (La magia de tu nueva arquitectura)
        Transaccion reciboLuz = Transaccion.builder()
                .apartamento(apartamento)
                .reserva(reserva)
                .tipo(TipoTransaccion.INGRESO)
                .categoria(CategoriaTransaccion.SUMINISTROS)
                .estado(EstadoTransaccion.PENDIENTE)
                .concepto("Factura Luz Marzo")
                .importe(new BigDecimal("45.50"))
                .fechaEmision(LocalDate.of(2026, 5, 10))
                .fechaVencimiento(LocalDate.of(2026, 5, 15))
                .build();
        transaccionRepository.save(reciboLuz);

        Transaccion reciboAgua = Transaccion.builder()
                .apartamento(apartamento)
                .reserva(reserva)
                .tipo(TipoTransaccion.INGRESO)
                .categoria(CategoriaTransaccion.SUMINISTROS)
                .estado(EstadoTransaccion.PENDIENTE)
                .concepto("Factura Agua Marzo")
                .importe(new BigDecimal("20.00"))
                .fechaEmision(LocalDate.of(2026, 5, 10))
                .fechaVencimiento(LocalDate.of(2026, 5, 15))
                .build();
        transaccionRepository.save(reciboAgua);

        log.info("¡Datos de prueba cargados con éxito! Ya puedes iniciar sesión en Angular con 'propietario@email.com' o 'inquilino@email.com' y contraseña '123456'.");
    }

    private void crearMesCompleto(Reserva r, int mes, int anio, BigDecimal alquiler, BigDecimal montoGasto, String conceptoGasto, CategoriaTransaccion catGasto) {

        // 1. Ingreso por alquiler (Asociado a la reserva del inquilino)
        Transaccion ingreso = Transaccion.builder()
                .apartamento(r.getApartamento())
                .reserva(r)
                .tipo(TipoTransaccion.INGRESO)
                .categoria(CategoriaTransaccion.ALQUILER)
                .estado(EstadoTransaccion.PAGADO)
                .concepto("Alquiler " + mes + "/" + anio)
                .importe(alquiler)
                .fechaEmision(LocalDate.of(anio, mes, 1))
                .fechaPago(LocalDate.of(anio, mes, 5))
                .build();
        transaccionRepository.save(ingreso);

        // 2. Gasto del piso (La reserva va en null porque es un gasto del propietario, no del inquilino)
        Transaccion gasto = Transaccion.builder()
                .apartamento(r.getApartamento())
                .reserva(null)
                .tipo(TipoTransaccion.GASTO)
                .categoria(catGasto)
                .estado(EstadoTransaccion.PAGADO)
                .concepto(conceptoGasto)
                .importe(montoGasto)
                .fechaEmision(LocalDate.of(anio, mes, 10))
                .fechaPago(LocalDate.of(anio, mes, 10))
                .build();
        transaccionRepository.save(gasto);
    }
}