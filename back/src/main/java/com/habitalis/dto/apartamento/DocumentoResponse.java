package com.habitalis.dto.apartamento;

import java.time.LocalDateTime;

public record DocumentoResponse(
        Long id,
        String rutaArchivo,
        String nombreOriginal,
        LocalDateTime fechaSubida
) {}