package com.apartmanagebackend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos_apartamento")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DocumentoApartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartamento_id", nullable = false)
    private Apartamento apartamento;

    @Column(nullable = false)
    private String rutaArchivo;

    @Column(nullable = false)
    private String nombreOriginal;

    @Column(nullable = false)
    private LocalDateTime fechaSubida;
}