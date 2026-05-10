package com.apartmanagebackend.domain;

import com.apartmanagebackend.domain.enums.EstadoApartamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "apartamentos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Apartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Usuario propietario;

    @Column(name = "nombre_interno", nullable = false, length = 100)
    private String nombreInterno;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoApartamento estado = EstadoApartamento.ACTIVO;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenesApartamento> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<ElementoInventario> inventario = new HashSet<>();

    @OneToMany(mappedBy = "apartamento")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Contrato> contratos = new HashSet<>();

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Incidencia> incidencias = new HashSet<>();

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaccion> transacciones = new HashSet<>();

    @OneToMany(mappedBy = "apartamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private List<DocumentoApartamento> documentos = new ArrayList<>();
}

