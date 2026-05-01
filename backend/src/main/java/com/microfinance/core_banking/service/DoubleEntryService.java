package com.microfinance.core_banking.service;

import com.microfinance.core_banking.dto.BalanceLine;
import java.util.List;

/**
 * Service responsible for validating double-entry balance lines.
 * This decouples the balance validation logic from the ComptabiliteExtensionService.
 */
public interface DoubleEntryService {
    void validerPieceEquilibree(List<BalanceLine> balanceLines);
}
