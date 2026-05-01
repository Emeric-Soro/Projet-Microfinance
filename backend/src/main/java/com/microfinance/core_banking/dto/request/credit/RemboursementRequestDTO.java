package com.microfinance.core_banking.dto.request.credit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// DTO pour enregistrer un remboursement d'echeance.
public record RemboursementRequestDTO(

		@NotNull(message = "Le montant du remboursement est obligatoire")
		@Positive(message = "Le montant doit etre strictement positif")
		BigDecimal montant
) {}
