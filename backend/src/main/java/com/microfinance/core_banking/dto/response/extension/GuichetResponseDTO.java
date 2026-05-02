package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Informations d'un guichet")
public class GuichetResponseDTO {
    @Schema(description = "Identifiant unique du guichet", example = "1")
    private Long idGuichet;

    @Schema(description = "Code du guichet", example = "GUI-001")
    private String codeGuichet;

    @Schema(description = "Nom du guichet", example = "Guichet 1")
    private String nomGuichet;

    @Schema(description = "Statut du guichet", example = "ACTIF")
    private String statut;

    @Schema(description = "Nom de l'agence rattachée", example = "Agence Dakar Plateau")
    private String nomAgence;
}
