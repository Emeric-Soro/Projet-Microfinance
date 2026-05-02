package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un rapport de conformité")
public class CreerRapportConformiteRequestDTO {
    @NotBlank(message = "Le type rapport est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type de rapport (obligatoire, max 50 caractères)", example = "KYC")
    private String typeRapport;

    @NotBlank(message = "La periode debut est obligatoire")
    @Schema(description = "Période début du rapport (obligatoire)", example = "2026-01-01")
    private String periodeDebut;

    @NotBlank(message = "La periode fin est obligatoire")
    @Schema(description = "Période fin du rapport (obligatoire)", example = "2026-03-31")
    private String periodeFin;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(max = 5000)
    @Schema(description = "Contenu du rapport (obligatoire, max 5000 caractères)", example = "Rapport de conformité KYC du T1 2026")
    private String contenu;

    @Size(max = 20)
    @Schema(description = "Statut du rapport (optionnel, max 20 caractères)", example = "BROUILLON")
    private String statut;
}
