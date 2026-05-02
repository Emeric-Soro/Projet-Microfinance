package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Informations d'une caisse")
public class CaisseResponseDTO {
    @Schema(description = "Identifiant unique de la caisse", example = "1")
    private Long idCaisse;

    @Schema(description = "Code de la caisse", example = "CAIS-001")
    private String codeCaisse;

    @Schema(description = "Libellé de la caisse", example = "Caisse principale")
    private String libelle;

    @Schema(description = "Agence rattachée", example = "Agence Dakar Plateau")
    private String agence;

    @Schema(description = "Guichet rattaché", example = "Guichet 1")
    private String guichet;

    @Schema(description = "Statut de la caisse", example = "ACTIF")
    private String statut;

    @Schema(description = "Solde théorique en XOF", example = "2000000.00")
    private BigDecimal soldeTheorique;
}
