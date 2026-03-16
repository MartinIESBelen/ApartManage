package com.example.apartmanagebackend.domain;

import com.example.apartmanagebackend.domain.enums.RolUsuario; // Asegúrate de renombrar la carpeta a 'enums'
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder; // Necesario para herencia
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Usamos SuperBuilder en lugar de Builder normal por la herencia
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Solo este campo se usa para equals/hashcode
    protected Long id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String telefono;

    @Column(name = "dni_pasaporte", length = 50)
    private String dniPasaporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Convertimos el enum (PROPIETARIO) en un rol que security extienda
        return List.of(new SimpleGrantedAuthority(rol.toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Estos 4 métodos controlan si la cuenta está bloqueada o caducada.
    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired()
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked()
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired()
    }

    @Override
    public boolean isEnabled() {
        return true;//UserDetails.super.isEnabled()
    }
}
