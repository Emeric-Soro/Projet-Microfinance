package com.microfinance.core_banking.dto.response.credit;

import java.math.BigDecimal;
import java.time.LocalDate;

// DTO de reponse pour les details d'un credit actif.
public record CreditResponseDTO(
		Long idCredit,
		String referenceCredit,
		String nomClient,
		String codeProduit,
		String libelleProduit,
		BigDecimal montantAccorde,
		BigDecimal montantRestantDu,
		BigDecimal tauxInteretAnnuel,
		Integer dureeMois,
		String methodeCalcul,
		BigDecimal fraisDossier,
		LocalDate dateDecaissement,
		LocalDate dateFinPrevue,
		String statutCredit,
		String numCompteDecaissement,
		String referenceDemande
) {}
