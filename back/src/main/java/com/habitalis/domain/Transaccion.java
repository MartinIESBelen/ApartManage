package com.habitalis.domain;

import com.habitalis.domain.enums.CategoriaTransaccion;
import com.habitalis.domain.enums.EstadoTransaccion;
import com.habitalis.domain.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    @ToString.Exclude
    private Apartamento apartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = true)
    @ToString.Exclude
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaTransaccion categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransaccion estado;

    @Column(nullable = false, length = 150)
    private String concepto;

    @Column(nullable = false)
    private BigDecimal importe;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;
}