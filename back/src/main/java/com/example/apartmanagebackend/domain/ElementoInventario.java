package com.example.apartmanagebackend.domain;

import com.example.apartmanagebackend.domain.enums.CategoriaItem;
import com.example.apartmanagebackend.domain.enums.EstadoItem;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "elementos_inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ElementoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Relación N:1 con Apartamento
    // Usamos @ToString.Exclude para evitar bucles infinitos al imprimir logs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    @ToString.Exclude
    private Apartamento apartamento;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private CategoriaItem categoria;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoItem estado = EstadoItem.BUENO;

    @Column(name = "precio_compra")
    private BigDecimal precioCompra;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;
}