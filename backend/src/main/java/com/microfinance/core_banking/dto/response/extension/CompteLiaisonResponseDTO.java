package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Compte de liaison entre agences")
public class CompteLiaisonResponseDTO {
    @Schema(description = "Identifiant unique du compte de liaison", example = "1")
    private Long idCompteLiaisonAgence;

    @Schema(description = "Agence source", example = "Agence Dakar Plateau")
    private String agenceSource;

    @Schema(description = "Agence destination", example = "Agence Thiès")
    private String agenceDestination;

    @Schema(description = "Identifiant du compte comptable", example = "1")
    private Long idCompteComptable;

    @Schema(description = "Numéro du compte comptable", example = "462000")
    private String numeroCompteComptable;

    @Schema(description = "Libellé du compte de liaison", example = "Liaison Dakar-Thiès")
    private String libelle;

    @Schema(description = "Indique si le compte de liaison est actif", example = "true")
    private Boolean actif;
}
