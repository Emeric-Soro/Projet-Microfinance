package com.soutra.microfinance.dto.request.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileMoneyRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String numCompte;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal montant;

    @NotBlank(message = "L'operateur Mobile Money est obligatoire")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String operateur;

    @NotBlank(message = "Le numero de telephone est obligatoire")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String telephone;
}
