package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MobileRecuResponseDTO(
        String reference,
        String type,
        BigDecimal montant,
        BigDecimal frais,
        String numCompte,
        LocalDateTime date,
        String statut
) {}
