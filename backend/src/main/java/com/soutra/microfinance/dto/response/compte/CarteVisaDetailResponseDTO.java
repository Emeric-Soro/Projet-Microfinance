package com.soutra.microfinance.dto.response.compte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detail complet d'une carte Visa.
 * Volontairement sans CVV ni PIN : le domaine applicatif ne les stocke pas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteVisaDetailResponseDTO {

    private Long idCarte;
    private String numeroCarteMasque;
    private LocalDate dateExpiration;
    private Boolean statut;
    private BigDecimal plafondJournalier;
    private String numCompte;
}
