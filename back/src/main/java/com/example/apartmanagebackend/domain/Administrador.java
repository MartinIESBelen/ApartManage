package com.example.apartmanagebackend.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Usamos SuperBuilder porque hereda de Usuario
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Administrador extends Usuario {

    @Column(name = "nivel_acceso", length = 50)
    @Builder.Default
    private String nivelAcceso = "total";
}