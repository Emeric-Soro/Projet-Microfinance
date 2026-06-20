package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MobileEcheanceResponseDTO(
        int numero,
        LocalDate dateEcheance,
        BigDecimal montant,
        BigDecimal principal,
        BigDecimal interets,
        BigDecimal soldeRestant,
        String statut
) {}
