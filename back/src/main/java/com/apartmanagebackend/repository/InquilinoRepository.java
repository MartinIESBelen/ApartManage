package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquilinoRepository extends JpaRepository<Inquilino,Long> {
}
