package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un fournisseur")
public class CreerFournisseurRequestDTO {
    @NotBlank(message = "Le code fournisseur est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code du fournisseur (obligatoire, max 20 caractères)", example = "FOUR-001")
    private String codeFournisseur;

    @NotBlank(message = "Le nom fournisseur est obligatoire")
    @Size(max = 100)
    @Schema(description = "Nom du fournisseur (obligatoire, max 100 caractères)", example = "Fournisseur SA")
    private String nomFournisseur;

    @Size(max = 50)
    @Schema(description = "Contact (optionnel, max 50 caractères)", example = "+221771234567")
    private String contact;

    @Size(max = 255)
    @Schema(description = "Adresse (optionnel, max 255 caractères)", example = "Dakar, Sénégal")
    private String adresse;

    @Size(max = 100)
    @Schema(description = "Email (optionnel, max 100 caractères)", example = "contact@fournisseur.sn")
    private String email;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
