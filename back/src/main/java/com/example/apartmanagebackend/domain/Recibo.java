package com.example.apartmanagebackend.domain;

import com.example.apartmanagebackend.domain.enums.EstadoRecibo;
import com.example.apartmanagebackend.domain.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recibos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    @ToString.Exclude
    private Reserva reserva;

    private Integer mes;
    private Integer anio;

    @Column(name = "monto_alquiler")
    private BigDecimal montoAlquiler;

    @Column(name = "monto_luz")
    private BigDecimal montoLuz;

    @Column(name = "monto_agua")
    private BigDecimal montoAgua;

    @Column(name = "total_pagar")
    private BigDecimal totalPagar;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoRecibo estado = EstadoRecibo.PENDIENTE;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago = MetodoPago.NO_ESPECIFICADO;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;
}