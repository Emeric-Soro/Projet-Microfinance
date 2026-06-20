package com.soutra.microfinance.dto.response.mobile;

public record MobileBeneficiaireResponseDTO(
        Long id,
        String nom,
        String prenom,
        String compteBeneficiaire,
        String banque
) {}
