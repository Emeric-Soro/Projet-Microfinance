package com.soutra.microfinance.dto.response.parametrage;

public record ParametreSystemeResponseDTO(
    Long id,
    String code,
    String libelle,
    String valeur,
    String typeValeur,
    String description,
    Boolean modifiable
) {}
