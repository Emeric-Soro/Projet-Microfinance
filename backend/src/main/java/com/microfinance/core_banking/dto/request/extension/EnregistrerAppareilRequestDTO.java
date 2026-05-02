package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête d'enregistrement d'un appareil")
public class EnregistrerAppareilRequestDTO {
    @NotBlank(message = "Le code appareil est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de l'appareil (obligatoire, max 20 caractères)", example = "APP-001")
    private String codeAppareil;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé de l'appareil (obligatoire, max 100 caractères)", example = "iPhone de Jean Dupont")
    private String libelle;

    @NotBlank(message = "Le type appareil est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'appareil (obligatoire, max 50 caractères)", example = "SMARTPHONE")
    private String typeAppareil;

    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private Long idAgence;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
