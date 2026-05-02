package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un opérateur mobile money")
public class CreerOperateurRequestDTO {
    @NotBlank(message = "Le code operateur est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de l'opérateur (obligatoire, max 20 caractères)", example = "OP-WAVE")
    private String codeOperateur;

    @NotBlank(message = "Le nom operateur est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom de l'opérateur (obligatoire, max 100 caractères)", example = "Wave Sénégal")
    private String nomOperateur;

    @NotBlank(message = "Le type operateur est obligatoire")
    @Size(max = 50)
    @Schema(description = "Type d'opérateur (obligatoire, max 50 caractères)", example = "MOBILE_MONEY")
    private String typeOperateur;

    @Size(max = 50)
    @Schema(description = "Contact (optionnel, max 50 caractères)", example = "+221339091234")
    private String contact;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
