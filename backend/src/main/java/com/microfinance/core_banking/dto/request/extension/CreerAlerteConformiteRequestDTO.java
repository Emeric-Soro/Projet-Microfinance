package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une alerte de conformité")
public class CreerAlerteConformiteRequestDTO {
    @NotBlank(message = "Le type alerte est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'alerte (obligatoire, max 50 caractères)", example = "SANCTION")
    private String typeAlerte;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000)
    @Schema(description = "Description de l'alerte (obligatoire, max 1000 caractères)", example = "Correspondance trouvée avec une liste de sanctions")
    private String description;

    @NotBlank(message = "La gravite est obligatoire")
    @Size(max = 20)
    @Schema(description = "Gravité de l'alerte (obligatoire, max 20 caractères)", example = "CRITIQUE")
    private String gravite;

    @Schema(description = "Identifiant du client concerné (optionnel)", example = "1")
    private Long idClient;

    @Size(max = 20)
    @Schema(description = "Statut de l'alerte (optionnel, max 20 caractères)", example = "OUVERTE")
    private String statut;
}
