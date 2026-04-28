package com.microfinance.core_banking.service.operation.fees;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionFeeCalculatorTest {

    @Test
    void shouldUseMatchingStrategyByTransactionCode() {
        TransactionFeeStrategy retraitStrategy = new TransactionFeeStrategy() {
            @Override
            public String codeTypeTransaction() {
                return "RETRAIT";
            }

            @Override
            public BigDecimal calculerFrais(BigDecimal montant) {
                return montant.multiply(new BigDecimal("0.02"));
            }
        };

        TransactionFeeCalculator calculator = new TransactionFeeCalculator(List.of(retraitStrategy));
        BigDecimal frais = calculator.calculerFrais("RETRAIT", new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("2.00").compareTo(frais));
    }

    @Test
    void shouldFailWhenNoStrategyIsRegisteredForCode() {
        TransactionFeeCalculator calculator = new TransactionFeeCalculator(List.of());

        assertThrows(
                IllegalStateException.class,
                () -> calculator.calculerFrais("INCONNU", new BigDecimal("100.00"))
        );
    }
}
