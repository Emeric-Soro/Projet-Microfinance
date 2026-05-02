package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Garantie associée à un crédit")
public class GarantieCreditResponseDTO {
    @Schema(description = "Identifiant unique de la garantie", example = "1")
    private Long idGarantieCredit;

    @Schema(description = "Type de garantie", example = "BIEN_IMMOBILIER")
    private String typeGarantie;

    @Schema(description = "Description de la garantie", example = "Maison sise à Dakar")
    private String description;

    @Schema(description = "Valeur estimée de la garantie en XOF", example = "25000000.00")
    private BigDecimal valeur;

    @Schema(description = "Valeur nantie (montant retenu) en XOF", example = "15000000.00")
    private BigDecimal valeurNantie;

    @Schema(description = "Statut de la garantie", example = "ACTIF")
    private String statut;
}
