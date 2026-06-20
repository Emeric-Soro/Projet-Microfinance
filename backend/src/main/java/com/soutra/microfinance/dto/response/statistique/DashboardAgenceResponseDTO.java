package com.soutra.microfinance.dto.response.statistique;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardAgenceResponseDTO(
        Long agenceId,
        String agenceNom,
        LocalDate date,
        long totalClientsJour,
        BigDecimal totalDepotsJour,
        BigDecimal totalRetraitsJour,
        BigDecimal totalCreditsAccordesMois,
        BigDecimal encoursCredit,
        BigDecimal soldeCaisse,
        long nbOperationsEnAttente
) {}
