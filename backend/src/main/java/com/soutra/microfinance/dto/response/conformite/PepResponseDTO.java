package com.soutra.microfinance.dto.response.conformite;

import java.time.LocalDateTime;

public record PepResponseDTO(
        Long idPep,
        Long idClient,
        String nomComplet,
        String fonction,
        String pays,
        String niveauRisque,
        String statut,
        LocalDateTime dateDeclaration
) {}
