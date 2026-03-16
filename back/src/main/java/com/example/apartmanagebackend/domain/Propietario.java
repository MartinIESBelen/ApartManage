package com.example.apartmanagebackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "propietarios")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true) // Incluye los campos del padre al imprimir
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Propietario extends Usuario {

    @Column(length = 50)
    private String iban;

    @Column(name = "direccion_fiscal", columnDefinition = "TEXT")
    private String direccionFiscal;

    //1:N (Un propietario tiene muchos apartamentos)
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Apartamento> apartamentos = new HashSet<>();
}