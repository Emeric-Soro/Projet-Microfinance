package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un agent")
public class CreerAgentRequestDTO {
    @NotBlank(message = "Le code agent est obligatoire")
    @Schema(description = "Code de l'agent (obligatoire)", example = "AGT-001")
    private String codeAgent;

    @NotBlank(message = "Le nom agent est obligatoire")
    @Schema(description = "Nom de l'agent (obligatoire)", example = "Agent Dakar")
    private String nomAgent;

    @Schema(description = "Téléphone de l'agent (optionnel)", example = "+221771234567")
    private String telephone;

    @Schema(description = "Adresse de l'agent (optionnel)", example = "123 Rue Liberté, Dakar")
    private String adresse;

    @NotBlank(message = "Le type agent est obligatoire")
    @Schema(description = "Type d'agent (obligatoire)", example = "COMMERCIAL")
    private String typeAgent;

    @NotNull(message = "L'id de l'agence de rattachement est obligatoire")
    @Schema(description = "Identifiant de l'agence de rattachement (obligatoire)", example = "1")
    private Long idAgenceRattachement;
}
