package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

public record RegleDerogationEscaladeResponseDTO(
        Long id,
        String type,
        String critere,
        String action,
        BigDecimal seuil,
        String roleApprobateur
) {}
