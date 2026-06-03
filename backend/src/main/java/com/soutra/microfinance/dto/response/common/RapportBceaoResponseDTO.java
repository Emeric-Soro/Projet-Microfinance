package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RapportBceaoResponseDTO(
        int trimestre,
        int annee,
        BigDecimal totalBilan,
        BigDecimal fondsPropres,
        BigDecimal creditsAccordes,
        BigDecimal depotsCollectes,
        BigDecimal ratioSolvabilite
) {}
