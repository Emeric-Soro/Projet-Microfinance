package com.soutra.microfinance.dto.response.parametrage;

import java.time.LocalDateTime;

public record TarificationResponseDTO(
    Long idParametre,
    String code,
    String libelle,
    String categorie,
    String valeur,
    String typeValeur,
    Boolean actif,
    LocalDateTime createdAt
) {}
