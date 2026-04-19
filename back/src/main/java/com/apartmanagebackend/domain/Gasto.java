package com.apartmanagebackend.domain;

import com.apartmanagebackend.domain.enums.CategoriaGasto;
import com.apartmanagebackend.domain.enums.TipoGasto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Un gasto siempre pertenece a un apartamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    @ToString.Exclude
    private Apartamento apartamento;

    @Column(nullable = false, length = 100)
    private String concepto; // Ej: "Recibo Comunidad Enero", "Arreglo grifo"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaGasto categoria;

    @Column(nullable = false)
    private BigDecimal importe;

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gasto", nullable = false)
    private TipoGasto tipoGasto;


}