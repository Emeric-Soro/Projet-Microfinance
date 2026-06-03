package com.soutra.microfinance.dto.response.common;

import java.time.LocalDateTime;

public record RapportExportResponseDTO(
        String type,
        String format,
        String contenu,
        String nomFichier,
        LocalDateTime dateGeneration
) {}
