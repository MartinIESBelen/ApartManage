package com.habitalis.repository;

import com.habitalis.domain.DocumentoApartamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoApartamentoRepository extends JpaRepository<DocumentoApartamento, Long> {

    List<DocumentoApartamento> findByApartamentoId(Long apartamentoId);
}