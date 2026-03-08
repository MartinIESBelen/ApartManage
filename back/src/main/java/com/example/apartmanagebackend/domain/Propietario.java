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

    // RELACIÓN 1:N (Un propietario tiene muchos apartamentos)
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Para inicializar el HashSet vacío
    @ToString.Exclude // Evita bucles infinitos al imprimir
    @JsonIgnore // Evita que al pedir el propietario te devuelva toda la cadena de objetos infinitamente
    private Set<Apartamento> apartamentos = new HashSet<>();
}