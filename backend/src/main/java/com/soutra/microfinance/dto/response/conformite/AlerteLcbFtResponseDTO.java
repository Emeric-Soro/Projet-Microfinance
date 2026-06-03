package com.soutra.microfinance.dto.response.conformite;

import java.time.LocalDateTime;

public record AlerteLcbFtResponseDTO(
        Long idAlerte,
        Long idClient,
        String typeAlerte,
        String description,
        String niveauRisque,
        String statut,
        LocalDateTime dateCreation
) {}
