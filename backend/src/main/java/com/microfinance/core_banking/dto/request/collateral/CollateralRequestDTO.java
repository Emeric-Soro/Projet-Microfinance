package com.microfinance.core_banking.dto.request.collateral;

import com.microfinance.core_banking.entity.Collateral;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'une garantie (collatéral) pour un prêt")
public class CollateralRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    @Schema(description = "Identifiant du prêt (obligatoire)", example = "1")
    private Long loanFacilityId;

    @NotNull(message = "Le type de collateral est obligatoire")
    @Schema(description = "Type de garantie (obligatoire)", example = "BIEN_IMMOBILIER")
    private Collateral.CollateralType collateralType;

    @Schema(description = "Description de la garantie (optionnel)", example = "Maison à Dakar")
    private String description;

    @NotNull(message = "La valeur est obligatoire")
    @Schema(description = "Valeur estimée de la garantie (obligatoire)", example = "50000000.00")
    private BigDecimal value;

    @NotNull(message = "Le statut de lien est obligatoire")
    @Schema(description = "Statut du lien sur la garantie (obligatoire)", example = "LIBRE")
    private Collateral.LienStatus lienStatus;
}
