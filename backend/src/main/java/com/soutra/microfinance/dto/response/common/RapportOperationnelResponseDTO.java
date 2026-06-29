package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;
import java.util.List;

public record RapportOperationnelResponseDTO(
        String periode,
        BigDecimal totalDepots,
        BigDecimal totalRetraits,
        BigDecimal totalVirements,
        long nbTransactions,
        BigDecimal montantTotalFrais,
        BigDecimal tauxReussite,
        List<GuichetierActivityDTO> activiteGuichetiers,
        List<ActiviteJournaliereRapportDTO> detailsJournaliers
) {}
