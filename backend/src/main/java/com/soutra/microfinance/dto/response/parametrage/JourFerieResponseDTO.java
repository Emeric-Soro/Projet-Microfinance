package com.soutra.microfinance.dto.response.parametrage;

public record JourFerieResponseDTO(
        Long id,
        String nom,
        String dateJour,
        Boolean recurrent,
        String pays
) {}
