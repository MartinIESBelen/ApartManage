package com.example.apartmanagebackend.domain;

import com.example.apartmanagebackend.domain.enums.EstadoIncidencia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "incidencias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Incidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    private Apartamento apartamento;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoIncidencia estado = EstadoIncidencia.ABIERTA;

    @CreationTimestamp
    @Column(name = "fecha_reporte", updatable = false)
    private LocalDateTime fechaReporte;
}