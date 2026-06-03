package com.soutra.microfinance.dto.response.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DerogationResponseDTO(
        Long id,
        String reference,
        String typeDerogation,
        String description,
        String motif,
        BigDecimal montantConcerne,
        String statut,
        LocalDateTime dateCreation,
        String creePar,
        LocalDateTime dateTraitement,
        String traitePar
) {}
