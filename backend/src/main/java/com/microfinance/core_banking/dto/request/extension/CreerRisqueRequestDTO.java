package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un risque")
public class CreerRisqueRequestDTO {
    @NotBlank(message = "Le code risque est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du risque (obligatoire, max 20 caractères)", example = "RISQ-001")
    private String codeRisque;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du risque (obligatoire, max 100 caractères)", example = "Risque de crédit")
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    @Schema(description = "Catégorie (obligatoire, max 50 caractères)", example = "CREDIT")
    private String categorie;

    @NotBlank(message = "La probabilite est obligatoire")
    @Size(max = 20)
    @Schema(description = "Probabilité (obligatoire, max 20 caractères)", example = "ELEVEE")
    private String probabilite;

    @NotBlank(message = "L'impact est obligatoire")
    @Size(max = 20)
    @Schema(description = "Impact (obligatoire, max 20 caractères)", example = "MAJEUR")
    private String impact;

    @NotBlank(message = "Le niveau risque est obligatoire")
    @Size(max = 20)
    @Schema(description = "Niveau de risque (obligatoire, max 20 caractères)", example = "CRITIQUE")
    private String niveauRisque;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "OUVERT")
    private String statut;
}
