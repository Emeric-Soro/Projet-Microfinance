package com.microfinance.core_banking.dto.response.credit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de reponse pour une demande de credit.
public record DemandeCreditResponseDTO(
		Long idDemande,
		String referenceDemande,
		String nomClient,
		String codeProduit,
		String libelleProduit,
		BigDecimal montantDemande,
		Integer dureeSouhaitee,
		String objetCredit,
		LocalDate dateDemande,
		LocalDateTime dateDecision,
		String statutDemande,
		String motifRejet,
		Integer scoreClient,
		String nomAgentCredit
) {}
