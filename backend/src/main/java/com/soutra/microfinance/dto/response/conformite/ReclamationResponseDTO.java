package com.soutra.microfinance.dto.response.conformite;

import java.time.LocalDateTime;

public record ReclamationResponseDTO(
        Long idReclamation,
        String reference,
        Long idClient,
        String typeReclamation,
        String description,
        String statut,
        String priorite,
        LocalDateTime dateCreation,
        LocalDateTime dateTraitement
) {}
