package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Informations d'une agence")
public class AgenceResponseDTO {
    @Schema(description = "Identifiant unique de l'agence", example = "1")
    private Long idAgence;

    @Schema(description = "Code de l'agence", example = "AG-001")
    private String codeAgence;

    @Schema(description = "Nom de l'agence", example = "Agence Dakar Plateau")
    private String nomAgence;

    @Schema(description = "Adresse de l'agence", example = "12 Avenue de la République, Dakar")
    private String adresse;

    @Schema(description = "Numéro de téléphone", example = "+221331234567")
    private String telephone;

    @Schema(description = "Statut de l'agence", example = "ACTIF")
    private String statut;

    @Schema(description = "Nom de la région de rattachement", example = "Dakar")
    private String nomRegion;
}
