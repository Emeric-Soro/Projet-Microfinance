package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Informations d'un fournisseur")
public class FournisseurResponseDTO {
    @Schema(description = "Identifiant unique du fournisseur", example = "1")
    private Long idFournisseur;

    @Schema(description = "Code du fournisseur", example = "FRS-001")
    private String codeFournisseur;

    @Schema(description = "Nom du fournisseur", example = "Fournitures Dakar SARL")
    private String nom;

    @Schema(description = "Contact principal", example = "M. Diallo")
    private String contact;

    @Schema(description = "Numéro de téléphone", example = "+221771234567")
    private String telephone;

    @Schema(description = "Adresse email", example = "contact@fournituresdakar.sn")
    private String email;

    @Schema(description = "Statut du fournisseur", example = "ACTIF")
    private String statut;
}
