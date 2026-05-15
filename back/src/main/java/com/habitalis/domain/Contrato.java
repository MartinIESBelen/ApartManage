package com.habitalis.domain;

import com.habitalis.domain.enums.EstadoContrato;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contratos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Apartamento apartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquilino_id")
    @ToString.Exclude
    @JsonIgnore
    private Usuario inquilino;

    @Column(name = "codigo_vinculacion", nullable = false, unique = true, length = 20)
    private String codigoVinculacion;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "precio_base_alquiler", nullable = false)
    private BigDecimal precioBaseAlquiler;

    @Column(name = "fianza", precision = 10, scale = 2)
    private BigDecimal fianza;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoContrato estado = EstadoContrato.PENDIENTE;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "contrato_pdf")
    private String contratoPdf;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaccion> transacciones = new HashSet<>();
}