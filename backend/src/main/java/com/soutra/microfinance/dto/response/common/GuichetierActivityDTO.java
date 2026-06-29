package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record GuichetierActivityDTO(
        String login,
        String nom,
        long nbOperations,
        BigDecimal volumeDepots,
        BigDecimal volumeRetraits,
        BigDecimal volumeVirements,
        BigDecimal fraisGeneres
) {}
