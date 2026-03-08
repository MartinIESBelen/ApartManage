package com.example.apartmanagebackend.repository;

import com.example.apartmanagebackend.domain.Inquilino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquilinoRepository extends JpaRepository<Inquilino,Long> {
}
