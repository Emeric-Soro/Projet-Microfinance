package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de décision sur une demande de crédit")
public class DeciderDemandeCreditRequestDTO {
    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    @Schema(description = "Statut de la décision (obligatoire, max 20 caractères)", example = "APPROUVE")
    private String statut;

    @Size(max = 1000)
    @Schema(description = "Avis du comité de crédit (optionnel, max 1000 caractères)", example = "Dossier conforme à la politique de crédit")
    private String avisComite;

    @Size(max = 20)
    @Schema(description = "Décision finale (optionnel, max 20 caractères)", example = "APPROUVE")
    private String decisionFinale;
}
