package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête de passage en perte d'un crédit")
public class PassagePerteCreditRequestDTO {

    @NotNull(message = "L'id du credit est obligatoire")
    @Schema(description = "Identifiant du crédit (obligatoire)", example = "1")
    private Long idCredit;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Créance irrécouvrable après épuisement des voies de recouvrement")
    private String commentaire;
}
