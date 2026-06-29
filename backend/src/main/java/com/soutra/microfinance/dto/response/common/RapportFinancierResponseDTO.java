package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

public record RapportFinancierResponseDTO(
        String periode,
        BigDecimal totalActifs,
        BigDecimal totalPassifs,
        BigDecimal produitNet,
        BigDecimal margeInterets,
        BigDecimal ratioEfficacite,
        Map<String, BigDecimal> bilanActif,
        Map<String, BigDecimal> bilanPassif,
        Map<String, BigDecimal> cpcProduits,
        Map<String, BigDecimal> cpcCharges,
        List<String> evolutionLabels,
        List<BigDecimal> evolutionActifs,
        List<BigDecimal> evolutionPassifs,
        List<BigDecimal> evolutionProduits,
        List<BigDecimal> evolutionMarges
) {}
