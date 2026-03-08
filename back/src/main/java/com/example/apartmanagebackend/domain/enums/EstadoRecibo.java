package com.example.apartmanagebackend.domain.enums;

public enum EstadoRecibo {
    PENDIENTE, // El sistema sabe que toca cobrar, pero aún no has subido las facturas
    EMITIDO,           // Ya has enviado el recibo al inquilino
    PAGADO,            // Dinero en el banco
    VENCIDO            // No pagaron a tiempo
}