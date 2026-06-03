package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RapportCaisseResponseDTO(
        String periode,
        long totalCaissesOuvertes,
        BigDecimal totalEncaissements,
        BigDecimal totalDecaissements,
        BigDecimal ecartsConstates
) {}
