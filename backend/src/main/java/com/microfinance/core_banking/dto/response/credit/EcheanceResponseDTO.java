package com.microfinance.core_banking.dto.response.credit;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de reponse pour une ligne du tableau d'amortissement.
public record EcheanceResponseDTO(
		Long idEcheance,
		Integer numeroEcheance,
		LocalDate dateEcheance,
		BigDecimal montantCapital,
		BigDecimal montantInteret,
		BigDecimal montantTotal,
		BigDecimal montantPenalite,
		BigDecimal montantPaye,
		LocalDate datePaiement,
		Boolean estPayee
) {}
