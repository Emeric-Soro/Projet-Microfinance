package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RapportCreditsResponseDTO(
        String periode,
        long totalCredits,
        BigDecimal montantTotalAccorde,
        BigDecimal encoursTotal,
        BigDecimal par30,
        BigDecimal par90,
        BigDecimal tauxImpayes
) {}
