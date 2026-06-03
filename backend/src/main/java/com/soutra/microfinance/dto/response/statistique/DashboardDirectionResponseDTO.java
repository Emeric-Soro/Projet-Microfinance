package com.soutra.microfinance.dto.response.statistique;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardDirectionResponseDTO(
        LocalDate date,
        long nbAgencesActives,
        long totalClientsReseau,
        BigDecimal totalDepotsReseau,
        BigDecimal totalRetraitsReseau,
        BigDecimal totalCreditsReseau,
        BigDecimal encoursTotalReseau,
        BigDecimal parGlobal,
        BigDecimal ratioEfficaciteGlobal,
        long nbUtilisateursActifs
) {}
