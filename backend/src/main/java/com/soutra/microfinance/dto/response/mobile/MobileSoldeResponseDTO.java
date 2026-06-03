package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;

public record MobileSoldeResponseDTO(
        Long idCompte,
        String numCompte,
        String libelleType,
        BigDecimal solde,
        String statut
) {}
