package com.soutra.microfinance.dto.response.statistique;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActiviteJournaliereDTO(
        LocalDate date,
        BigDecimal depots,
        BigDecimal retraits
) {}
