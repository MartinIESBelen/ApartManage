package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropietarioRepository extends JpaRepository<Propietario,Long> {
}
