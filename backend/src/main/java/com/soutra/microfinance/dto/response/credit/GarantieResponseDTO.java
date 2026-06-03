package com.soutra.microfinance.dto.response.credit;

import java.math.BigDecimal;

public record GarantieResponseDTO(
        Long idGarantie,
        String typeGarantie,
        String description,
        BigDecimal valeurEstimee,
        Boolean estActive
) {}
