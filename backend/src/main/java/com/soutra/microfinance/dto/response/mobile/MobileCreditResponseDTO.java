package com.soutra.microfinance.dto.response.mobile;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MobileCreditResponseDTO(
        Long idCredit,
        String reference,
        BigDecimal montantAccorde,
        BigDecimal montantRestantDu,
        BigDecimal taux,
        Integer dureeMois,
        String statut,
        LocalDate dateDecaissement
) {}
