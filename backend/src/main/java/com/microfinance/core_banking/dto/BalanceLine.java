package com.microfinance.core_banking.dto;

import java.math.BigDecimal;
import com.microfinance.core_banking.entity.SensEcriture;

/**
 * Data transfer object representing a single line in a double-entry balance
 * used for validation purposes. This decouples the validation logic from the
 * ComptabiliteExtensionService's internal ManualLine record.
 */
public record BalanceLine(
        String numeroCompte,
        SensEcriture sens,
        BigDecimal montant,
        String referenceAuxiliaire,
        String libelleAuxiliaire
) {
}
