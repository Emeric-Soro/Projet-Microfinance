package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActiviteJournaliereRapportDTO(
        LocalDate date,
        BigDecimal depots,
        BigDecimal retraits,
        BigDecimal virements
) {}
