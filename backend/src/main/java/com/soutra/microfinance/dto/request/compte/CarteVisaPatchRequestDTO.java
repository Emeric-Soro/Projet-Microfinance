package com.soutra.microfinance.dto.request.compte;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PATCH partiel d'une carte Visa.
 * Tous les champs sont optionnels : seuls les champs non null sont appliques.
 * Le CVV et le PIN ne sont JAMAIS exposes ni modifies via ce DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CarteVisaPatchRequestDTO {

    private BigDecimal plafondJournalier;

    private Boolean statut;

    private LocalDate dateExpiration;
}
