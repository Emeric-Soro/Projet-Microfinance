package com.microfinance.core_banking.dto.request.credit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO pour approuver ou rejeter une demande de credit.
public record DecisionCreditRequestDTO(

		@NotNull(message = "L'identifiant de la demande est obligatoire")
		Long idDemande,

		@NotBlank(message = "La decision est obligatoire (APPROUVEE ou REJETEE)")
		String decision,

		String motifRejet
) {}
