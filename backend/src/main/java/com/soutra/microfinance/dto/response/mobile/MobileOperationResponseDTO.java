package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MobileOperationResponseDTO(
        String reference,
        String type,
        BigDecimal montant,
        LocalDateTime date,
        String statut
) {}
