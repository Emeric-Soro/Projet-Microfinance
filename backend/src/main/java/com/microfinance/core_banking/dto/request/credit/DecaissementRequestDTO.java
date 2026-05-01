package com.microfinance.core_banking.dto.request.credit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO pour le decaissement d'un credit approuve.
public record DecaissementRequestDTO(

		@NotBlank(message = "Le numero de compte cible est obligatoire")
		String numCompteCible
) {}
