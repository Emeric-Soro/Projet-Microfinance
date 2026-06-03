package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RapportOperationnelResponseDTO(
        String periode,
        BigDecimal totalDepots,
        BigDecimal totalRetraits,
        BigDecimal totalVirements,
        long nbTransactions,
        BigDecimal montantTotalFrais
) {}
