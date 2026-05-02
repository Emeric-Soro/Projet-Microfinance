package com.microfinance.core_banking.service;

import com.microfinance.core_banking.dto.BalanceLine;
import com.microfinance.core_banking.entity.SensEcriture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DoubleEntryIntegrityTest {

    private final DoubleEntryService doubleEntryService = new DoubleEntryServiceImpl();

    @Test
    void balancedPieceShouldPass() {
        List<BalanceLine> lines = List.of(
                new BalanceLine("571000", SensEcriture.DEBIT, new BigDecimal("1000.00"), "CPT-001", "Caisse"),
                new BalanceLine("251000", SensEcriture.CREDIT, new BigDecimal("1000.00"), "CPT-001", "Depots clientele")
        );

        assertDoesNotThrow(() -> doubleEntryService.validerPieceEquilibree(lines));
    }

    @Test
    void unbalancedPieceShouldFail() {
        List<BalanceLine> lines = List.of(
                new BalanceLine("571000", SensEcriture.DEBIT, new BigDecimal("1000.00"), "CPT-001", "Caisse"),
                new BalanceLine("251000", SensEcriture.CREDIT, new BigDecimal("900.00"), "CPT-001", "Depots clientele")
        );

        assertThrows(IllegalStateException.class, () -> doubleEntryService.validerPieceEquilibree(lines));
    }

    @Test
    void debitOnlyPieceShouldFail() {
        List<BalanceLine> lines = List.of(
                new BalanceLine("571000", SensEcriture.DEBIT, new BigDecimal("1000.00"), "CPT-001", "Caisse")
        );

        assertThrows(IllegalStateException.class, () -> doubleEntryService.validerPieceEquilibree(lines));
    }

    @Test
    void creditOnlyPieceShouldFail() {
        List<BalanceLine> lines = List.of(
                new BalanceLine("251000", SensEcriture.CREDIT, new BigDecimal("1000.00"), "CPT-001", "Depots clientele")
        );

        assertThrows(IllegalStateException.class, () -> doubleEntryService.validerPieceEquilibree(lines));
    }
}
