package com.microfinance.core_banking.dto.request.compte;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Schema(description = "Requête de modification du plafond de découvert d'un compte")
public class ChangementDecouvertRequestDTO {

    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 50, message = "Le numero de compte ne doit pas depasser 50 caracteres")
    @Schema(description = "Numéro du compte (obligatoire, max 50 caractères)", example = "SN000012345678901")
    private String numCompte;

    @NotNull(message = "Le nouveau plafond est obligatoire")
    @PositiveOrZero(message = "Le nouveau plafond doit etre positif ou nul")
    @Schema(description = "Nouveau plafond de découvert (obligatoire, positif ou nul)", example = "500000.00")
    private BigDecimal nouveauPlafond;
}
