package com.microfinance.core_banking.dto.response.collateral;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'une garantie associée à une facilité de prêt")
public class CollateralResponseDTO {
    @Schema(description = "Identifiant unique de la garantie", example = "1")
    private Long id;

    @Schema(description = "Identifiant de la facilité de prêt associée", example = "1")
    private Long loanFacilityId;

    @Schema(description = "Type de garantie", example = "BIEN_IMMOBILIER")
    private String collateralType;

    @Schema(description = "Description détaillée de la garantie", example = "Maison sise à Dakar")
    private String description;

    @Schema(description = "Valeur estimée de la garantie en XOF", example = "25000000.00")
    private BigDecimal value;

    @Schema(description = "Statut du nantissement", example = "ACTIF")
    private String lienStatus;
}
