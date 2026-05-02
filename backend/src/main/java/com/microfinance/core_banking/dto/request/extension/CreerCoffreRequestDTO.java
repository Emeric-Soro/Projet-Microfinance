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
@Schema(description = "Requête de création d'un coffre")
public class CreerCoffreRequestDTO {
    @NotBlank(message = "Le code coffre est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du coffre (obligatoire, max 20 caractères)", example = "COF-001")
    private String codeCoffre;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du coffre (obligatoire, max 100 caractères)", example = "Coffre Agence Dakar")
    private String libelle;

    @NotNull(message = "L'id agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;

    @Size(max = 20)
    @Schema(description = "Statut du coffre (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
