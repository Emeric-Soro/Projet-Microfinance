package com.microfinance.core_banking.dto.request.credit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

// DTO pour soumettre une nouvelle demande de credit.
public record DemandeCreditRequestDTO(

		@NotNull(message = "L'identifiant du client est obligatoire")
		Long idClient,

		@NotBlank(message = "Le code du produit de credit est obligatoire")
		String codeProduitCredit,

		@NotNull(message = "Le montant demande est obligatoire")
		@Positive(message = "Le montant demande doit etre positif")
		BigDecimal montantDemande,

		@NotNull(message = "La duree souhaitee est obligatoire")
		@Min(value = 1, message = "La duree doit etre d'au moins 1 mois")
		Integer dureeSouhaitee,

		@NotBlank(message = "L'objet du credit est obligatoire")
		String objetCredit,

		Long idAgentCredit
) {}
