package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropietarioRepository extends JpaRepository<Propietario,Long> {
}
