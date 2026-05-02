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
@Schema(description = "Requête de création d'un guichet")
public class CreerGuichetRequestDTO {
    @NotBlank(message = "Le code guichet est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du guichet (obligatoire, max 20 caractères)", example = "GUI-001")
    private String codeGuichet;

    @NotBlank(message = "Le nom du guichet est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom du guichet (obligatoire, max 100 caractères)", example = "Guichet Dakar Centre")
    private String nomGuichet;

    @Size(max = 20)
    @Schema(description = "Statut du guichet (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;

    @NotNull(message = "L'id de l'agence est obligatoire")
    @Schema(description = "Identifiant de l'agence (obligatoire)", example = "1")
    private Long idAgence;
}
