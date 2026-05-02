package com.microfinance.core_banking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import com.microfinance.core_banking.entity.SensEcriture;

@Schema(description = "Ligne de balance comptable - résultat de l'appel à l'API balance")
public record BalanceLine(
        @Schema(description = "Numéro du compte comptable", example = "571000")
        String numeroCompte,
        @Schema(description = "Sens du solde (DEBITEUR/CREDITEUR)", example = "DEBITEUR")
        SensEcriture sens,
        @Schema(description = "Montant total du solde en XOF", example = "1500000.00")
        BigDecimal montant,
        @Schema(description = "Référence du tiers auxiliaire", example = "CLT-001")
        String referenceAuxiliaire,
        @Schema(description = "Libellé du tiers auxiliaire", example = "Client Dupont")
        String libelleAuxiliaire
) {
}
