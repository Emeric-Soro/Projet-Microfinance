package com.microfinance.core_banking.dto.request.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de virement entre deux comptes")
public class VirementRequestDTO {

    @NotBlank(message = "Le compte source est obligatoire")
    @Size(max = 50, message = "Le compte source ne doit pas depasser 50 caracteres")
    @Schema(description = "Numéro du compte source (obligatoire, max 50 caractères)", example = "SN000012345678901")
    private String compteSource;

    @NotBlank(message = "Le compte destination est obligatoire")
    @Size(max = 50, message = "Le compte destination ne doit pas depasser 50 caracteres")
    @Schema(description = "Numéro du compte destination (obligatoire, max 50 caractères)", example = "SN000098765432101")
    private String compteDestination;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre strictement positif")
    @Schema(description = "Montant du virement (obligatoire, strictement positif)", example = "50000.00")
    private BigDecimal montant;
}
