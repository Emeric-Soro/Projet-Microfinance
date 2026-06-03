package com.soutra.microfinance.dto.response.mobile;

public record MobileProfilResponseDTO(
        Long idClient,
        String nom,
        String prenom,
        String telephone,
        String email,
        String adresse,
        String profession
) {}
