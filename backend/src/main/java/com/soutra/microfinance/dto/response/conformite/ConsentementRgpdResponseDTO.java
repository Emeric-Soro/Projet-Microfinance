package com.soutra.microfinance.dto.response.conformite;

import java.time.LocalDateTime;

public record ConsentementRgpdResponseDTO(
        Long idConsentement,
        Long idClient,
        String finalite,
        Boolean consenti,
        LocalDateTime dateConsentement,
        LocalDateTime dateExpiration
) {}
