package com.habitalis.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagenes_apartamento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ImagenesApartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutaArchivo;

    private boolean esPrincipal;

    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id")
    private Apartamento apartamento;
}
