package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de décision KYC pour un client")
public class DecisionKycClientRequestDTO {

    @NotNull(message = "Le statut KYC cible est obligatoire")
    @Schema(description = "Statut KYC attribué (obligatoire)", example = "VALIDE")
    private StatutKycClient statutKyc;

    @NotNull(message = "Le niveau de risque est obligatoire")
    @Schema(description = "Niveau de risque attribué (obligatoire)", example = "FAIBLE")
    private NiveauRisqueClient niveauRisque;

    @Size(max = 500, message = "Le commentaire KYC ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire sur la décision KYC (optionnel, max 500 caractères)", example = "Dossier complet, client vérifié")
    private String commentaire;

    @NotBlank(message = "Le validateur KYC est obligatoire")
    @Size(max = 120, message = "Le validateur KYC ne doit pas depasser 120 caracteres")
    @Schema(description = "Nom du validateur KYC (obligatoire, max 120 caractères)", example = "Mamadou Diallo")
    private String validateurKyc;
}
