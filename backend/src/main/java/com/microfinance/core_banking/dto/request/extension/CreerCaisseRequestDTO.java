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
@Schema(description = "Requête de création d'une caisse")
public class CreerCaisseRequestDTO {
    @NotBlank(message = "Le code caisse est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de la caisse (obligatoire, max 20 caractères)", example = "CAI-001")
    private String codeCaisse;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé de la caisse (obligatoire, max 100 caractères)", example = "Caisse Principale")
    private String libelle;

    @NotBlank(message = "Le type caisse est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de caisse (obligatoire, max 50 caractères)", example = "PRINCIPALE")
    private String typeCaisse;

    @NotNull(message = "L'id agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;

    @Size(max = 20)
    @Schema(description = "Statut de la caisse (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
