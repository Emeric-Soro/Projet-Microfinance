package com.soutra.microfinance.dto.response.parametrage;

import java.time.LocalDateTime;

public record AgenceResponseDTO(
    Long idAgence,
    String codeAgence,
    String nom,
    String adresse,
    String telephone,
    String email,
    String chefAgenceNom,
    Boolean estActive,
    LocalDateTime createdAt
) {}
