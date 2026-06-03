package com.soutra.microfinance.dto.response.conformite;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RapportSarResponseDTO(
        Long idRapport,
        String reference,
        Long idClient,
        String typeAlerte,
        String description,
        BigDecimal montantSoupconne,
        String statut,
        LocalDateTime dateCreation,
        LocalDateTime dateTraitement,
        Boolean transmissionCentif
) {}
