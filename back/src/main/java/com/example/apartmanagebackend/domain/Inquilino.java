package com.example.apartmanagebackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "inquilinos")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Inquilino extends Usuario {

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "direccion_habitual", columnDefinition = "TEXT")
    private String direccionHabitual;

    // RELACIÓN 1:N (Un inquilino tiene muchas reservas/viajes)
    @OneToMany(mappedBy = "inquilino")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private Set<Reserva> reservas = new HashSet<>();
}