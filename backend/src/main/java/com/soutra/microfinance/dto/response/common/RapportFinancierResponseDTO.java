package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RapportFinancierResponseDTO(
        String periode,
        BigDecimal totalActifs,
        BigDecimal totalPassifs,
        BigDecimal produitNet,
        BigDecimal margeInterets,
        BigDecimal ratioEfficacite
) {}
