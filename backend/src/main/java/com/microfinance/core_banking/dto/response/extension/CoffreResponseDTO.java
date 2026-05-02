package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Informations d'un coffre")
public class CoffreResponseDTO {
    @Schema(description = "Identifiant unique du coffre", example = "1")
    private Long idCoffre;

    @Schema(description = "Code du coffre", example = "COF-001")
    private String codeCoffre;

    @Schema(description = "Libellé du coffre", example = "Coffre principal")
    private String libelle;

    @Schema(description = "Agence rattachée", example = "Agence Dakar Plateau")
    private String agence;

    @Schema(description = "Solde théorique en XOF", example = "10000000.00")
    private BigDecimal soldeTheorique;

    @Schema(description = "Statut du coffre", example = "ACTIF")
    private String statut;
}
