package com.example.apartmanagebackend.domain.enums;

public enum EstadoReserva {
    PENDIENTE,  // Creada pero el inquilino aún no ha entrado
    CONFIRMADA, // El inquilino ha hecho check-in y está dentro
    FINALIZADA, // Ya se fueron
    CANCELADA   // Se anuló antes de tiempo
}