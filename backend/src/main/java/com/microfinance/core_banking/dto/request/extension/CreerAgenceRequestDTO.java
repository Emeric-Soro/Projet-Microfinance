package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une agence")
public class CreerAgenceRequestDTO {
    @NotBlank(message = "Le code agence est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de l'agence (obligatoire, max 20 caractères)", example = "AG-001")
    private String codeAgence;

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom de l'agence (obligatoire, max 100 caractères)", example = "Agence Dakar Plateau")
    private String nomAgence;

    @Size(max = 255)
    @Schema(description = "Adresse de l'agence (optionnel, max 255 caractères)", example = "123 Avenue Lamine Gueye, Dakar")
    private String adresse;

    @Size(max = 30)
    @Schema(description = "Numéro de téléphone de l'agence (optionnel, max 30 caractères)", example = "+221338891234")
    private String telephone;

    @Size(max = 20)
    @Schema(description = "Statut de l'agence (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;

    @Schema(description = "Identifiant de la région (optionnel)", example = "1")
    private Long idRegion;
}
