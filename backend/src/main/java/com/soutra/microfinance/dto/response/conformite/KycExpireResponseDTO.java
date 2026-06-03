package com.soutra.microfinance.dto.response.conformite;

import java.time.LocalDate;

public record KycExpireResponseDTO(
        Long idClient,
        String codeClient,
        String nomComplet,
        LocalDate dateSoumissionKyc,
        String statutKyc,
        String niveauRisque,
        long joursEcoules
) {}
