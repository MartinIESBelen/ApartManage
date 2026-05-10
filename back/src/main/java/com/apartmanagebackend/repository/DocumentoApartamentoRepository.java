package com.apartmanagebackend.repository;

import com.apartmanagebackend.domain.DocumentoApartamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoApartamentoRepository extends JpaRepository<DocumentoApartamento, Long> {

    List<DocumentoApartamento> findByApartamentoId(Long apartamentoId);
}