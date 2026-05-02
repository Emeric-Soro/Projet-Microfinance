package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de validation d'une action")
public class CreerActionValidationRequestDTO {
    @NotBlank(message = "Le type action est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'action (obligatoire, max 50 caractères)", example = "VALIDATION")
    private String typeAction;

    @NotBlank(message = "La ressource est obligatoire")
    @Size(max = 50)
    @Schema(description = "Ressource concernée (obligatoire, max 50 caractères)", example = "CLIENT")
    private String ressource;

    @Size(max = 50)
    @Schema(description = "Référence de la ressource (optionnel, max 50 caractères)", example = "CLI-20260401-0001")
    private String referenceRessource;

    @Schema(description = "Données supplémentaires (optionnel)")
    private Map<String, Object> donnees;

    @Size(max = 500)
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Validation effectuée")
    private String commentaire;
}
