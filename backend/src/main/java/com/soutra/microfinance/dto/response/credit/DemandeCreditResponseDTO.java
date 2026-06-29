package com.soutra.microfinance.dto.response.credit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO de reponse pour une demande de credit.
public record DemandeCreditResponseDTO(
		Long idDemande,
		String referenceDemande,
		// Champs client
		String nomClient,
		String prenomClient,
		String codeClient,
		String telephone,
		String email,
		String statutKyc,
		BigDecimal revenuMensuel,
		// Champs produit
		String codeProduit,
		String libelleProduit,
		// Champs demande
		BigDecimal montantDemande,
		Integer dureeSouhaitee,
		String objetCredit,
		LocalDate dateDemande,
		LocalDateTime dateDecision,
		String statutDemande,
		String motifRejet,
		Integer scoreClient,
		// Champs agent
		String nomAgentCredit,
		String agentLogin
) {}
