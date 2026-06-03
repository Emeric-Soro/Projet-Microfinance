package com.soutra.microfinance.dto.response.statistique;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IndicateurTempsReelResponseDTO(
        long totalClients,
        long totalComptes,
        long totalTransactionsJour,
        BigDecimal montantTotalTransactionsJour,
        long nbSessionsActives,
        LocalDateTime derniereMiseAJour
) {}
