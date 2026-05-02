package com.microfinance.core_banking.service.operation.fees;

import com.microfinance.core_banking.service.extension.FiscaliteService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionFeeCalculatorTest {

    @Test
    void shouldUseMatchingStrategyByTransactionCode() {
        FiscaliteService fiscaliteService = mock(FiscaliteService.class);
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

        when(fiscaliteService.calculerTaxe(eq("TAXE_TRANSACTION"), any(BigDecimal.class), eq("RETRAIT")))
                .thenReturn(new BigDecimal("0.40"));
        TransactionFeeCalculator calculator = new TransactionFeeCalculator(List.of(retraitStrategy), fiscaliteService);
        BigDecimal frais = calculator.calculerFrais("RETRAIT", new BigDecimal("100.00"));

        assertEquals(0, new BigDecimal("2.40").compareTo(frais));
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
