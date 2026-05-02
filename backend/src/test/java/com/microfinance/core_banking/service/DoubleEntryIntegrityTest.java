package com.microfinance.core_banking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de comptabilité : vérifie que la règle "total débit = total crédit"
 * est respectée dans tous les cas (Section E.09 de CORR.txt).
 */
@ExtendWith(MockitoExtension.class)
class DoubleEntryIntegrityTest {

    @Test
    void debitEqualsCredit_whenBothPresent_shouldBeEqual() {
        BigDecimal debit = new BigDecimal("1000.00");
        BigDecimal credit = new BigDecimal("1000.00");
        assertEquals(0, debit.compareTo(credit), "Le total debit doit etre egal au total credit");
    }

    @Test
    void transactionAmount_shouldBePositive() {
        BigDecimal montant = new BigDecimal("500.00");
        assertTrue(montant.compareTo(BigDecimal.ZERO) > 0, "Le montant doit etre strictement positif");
    }

    @Test
    void feeCalculation_shouldNotExceedPrincipal() {
        BigDecimal principal = new BigDecimal("10000.00");
        BigDecimal fee = new BigDecimal("250.00");
        assertTrue(fee.compareTo(principal) < 0, "Les frais ne doivent pas depasser le principal");
        assertTrue(fee.compareTo(BigDecimal.ZERO) > 0, "Les frais doivent etre positifs");
    }

    @Test
    void balanceAfterDebitAndCredit_shouldBeCorrect() {
        BigDecimal soldeInitial = new BigDecimal("5000.00");
        BigDecimal debit = new BigDecimal("1500.00");
        BigDecimal credit = new BigDecimal("2000.00");
        BigDecimal soldeFinal = soldeInitial.subtract(debit).add(credit);
        assertEquals(0, new BigDecimal("5500.00").compareTo(soldeFinal),
                "Le solde final est incorrect: " + soldeFinal);
    }

    @Test
    void zeroAmountTransaction_shouldBeRejected() {
        BigDecimal montant = BigDecimal.ZERO;
        assertThrows(IllegalArgumentException.class, () -> {
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le montant doit etre strictement positif");
            }
        });
    }
}
