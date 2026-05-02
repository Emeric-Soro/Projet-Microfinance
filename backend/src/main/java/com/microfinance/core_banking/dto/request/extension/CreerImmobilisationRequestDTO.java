package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une immobilisation")
public class CreerImmobilisationRequestDTO {
    @NotBlank(message = "Le code immobilisation est obligatoire")
    @Size(max = 20)
    @Schema(description = "Code de l'immobilisation (obligatoire, max 20 caractères)", example = "IMM-001")
    private String codeImmobilisation;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé de l'immobilisation (obligatoire, max 100 caractères)", example = "Véhicule de service")
    private String libelle;

    @NotBlank(message = "La categorie est obligatoire")
    @Size(max = 50)
    @Schema(description = "Catégorie (obligatoire, max 50 caractères)", example = "VEHICULE")
    private String categorie;

    @NotNull(message = "La valeur d'acquisition est obligatoire")
    @Positive
    @Schema(description = "Valeur d'acquisition (obligatoire, positif)", example = "25000000.00")
    private BigDecimal valeurAcquisition;

    @NotBlank(message = "La date d'acquisition est obligatoire")
    @Schema(description = "Date d'acquisition (obligatoire)", example = "2026-01-15")
    private String dateAcquisition;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
