package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un employé (digital)")
public class CreerEmployeDigitalRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50)
    @Schema(description = "Nom de l'employé (obligatoire, max 50 caractères)", example = "Dupont")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 50)
    @Schema(description = "Prénom de l'employé (obligatoire, max 50 caractères)", example = "Jean")
    private String prenom;

    @Size(max = 100)
    @Schema(description = "Email (optionnel, max 100 caractères)", example = "jean.dupont@microfinance.sn")
    private String email;

    @Size(max = 50)
    @Schema(description = "Téléphone (optionnel, max 50 caractères)", example = "+221771234567")
    private String telephone;

    @Size(max = 100)
    @Schema(description = "Fonction (optionnel, max 100 caractères)", example = "Agent de crédit")
    private String fonction;

    @Schema(description = "Identifiant de l'agence (optionnel)", example = "1")
    private Long idAgence;

    @Size(max = 20)
    @Schema(description = "Statut (optionnel, max 20 caractères)", example = "ACTIF")
    private String statut;
}
