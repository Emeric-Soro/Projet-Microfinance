package com.soutra.microfinance.dto.response.common;

import java.time.LocalDateTime;

public record EscaladeResponseDTO(
        Long id,
        String reference,
        String typeEscalade,
        String description,
        String niveau,
        String statut,
        LocalDateTime dateCreation,
        String creePar,
        LocalDateTime dateTraitement,
        String traitePar
) {}
