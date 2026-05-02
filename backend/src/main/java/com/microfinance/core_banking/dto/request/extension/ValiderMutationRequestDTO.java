package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de validation d'une mutation")
public class ValiderMutationRequestDTO {
    @Schema(description = "Décision (optionnel)", example = "APPROUVE")
    private String decision;
    @Schema(description = "Commentaire de validation (optionnel)", example = "Mutation approuvée")
    private String commentaireValidation;
    @Schema(description = "Identifiant du validateur (optionnel)", example = "1")
    private Long idValidateur;
}
