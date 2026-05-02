package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de changement de statut d'un ordre")
public class ChangerStatutOrdreRequestDTO {
    @NotBlank(message = "Le nouveau statut est obligatoire")
    @Size(max = 20)
    @Schema(description = "Nouveau statut (obligatoire, max 20 caractères)", example = "VALIDE")
    private String nouveauStatut;

    @Size(max = 500)
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Validation effectuée")
    private String commentaire;

    @NotNull(message = "L'id validateur est obligatoire")
    @Schema(description = "Identifiant du validateur (obligatoire)", example = "1")
    private Long idValidateur;
}
