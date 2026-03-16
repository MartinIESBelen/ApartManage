package com.example.apartmanagebackend.domain;

import com.example.apartmanagebackend.domain.enums.EstadoApartamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "apartamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Apartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // IMPORTANTE: Al imprimir un apartamento, no queremos imprimir el propietario completo
    // para evitar bucles si el propietario tiene lista de apartamentos.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    @ToString.Exclude
    private Propietario propietario;

    @Column(name = "nombre_interno", nullable = false, length = 100)
    private String nombreInterno;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default // Para que el Builder use este valor por defecto
    private EstadoApartamento estado = EstadoApartamento.ACTIVO;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    // RELACIÓN 1:N con Inventario
    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<ElementoInventario> inventario = new HashSet<>();

    // RELACIÓN 1:N con Reservas
    @OneToMany(mappedBy = "apartamento")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Reserva> reservas = new HashSet<>();

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Incidencia> incidencias = new HashSet<>();
}