package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de paiement POS par carte bancaire")
public class PaiementPosRequestDTO {

    @NotNull(message = "L'identifiant de la carte est obligatoire")
    @Schema(description = "Identifiant de la carte bancaire", example = "1")
    private Long idCarte;

    @NotBlank(message = "Le code PIN est obligatoire")
    @Size(min = 4, max = 6, message = "Le code PIN doit contenir entre 4 et 6 caractères")
    @Schema(description = "Code PIN de la carte", example = "1234")
    private String pin;
}
