package com.microfinance.core_banking.dto.response.credit;

import java.math.BigDecimal;
import java.util.List;

// DTO de reponse pour le tableau d'amortissement complet avec resume.
public record TableauAmortissementResponseDTO(
		String referenceCredit,
		BigDecimal montantAccorde,
		BigDecimal tauxInteretAnnuel,
		Integer dureeMois,
		String methodeCalcul,
		BigDecimal totalInterets,
		BigDecimal coutTotal,
		List<EcheanceResponseDTO> echeances
) {}
