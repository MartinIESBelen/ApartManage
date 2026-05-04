package com.apartmanagebackend.domain;

import com.apartmanagebackend.domain.enums.CategoriaTransaccion;
import com.apartmanagebackend.domain.enums.EstadoTransaccion;
import com.apartmanagebackend.domain.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Obligatorio: Toda transacción pertenece a un piso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    @ToString.Exclude
    private Apartamento apartamento;

    // Opcional: Solo si va ligado a un inquilino concreto (Alquiler, factura de luz, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = true)
    @ToString.Exclude
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo; // INGRESO o GASTO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaTransaccion categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransaccion estado;

    @Column(nullable = false, length = 150)
    private String concepto; // Ej: "Alquiler Mayo", "Termo roto"

    @Column(nullable = false)
    private BigDecimal importe;

    @Column(columnDefinition = "TEXT")
    private String comentario; // El textArea opcional del formulario

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision; // Cuándo se registró

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento; // Día límite (opcional, útil para inquilinos)

    @Column(name = "fecha_pago")
    private LocalDate fechaPago; // Cuándo se hizo efectivo el pago
}