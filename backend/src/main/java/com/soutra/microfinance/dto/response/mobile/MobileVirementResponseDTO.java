package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;

public record MobileVirementResponseDTO(
        String reference,
        BigDecimal montant,
        String compteSource,
        String compteDestination,
        String statut,
        String message
) {}
