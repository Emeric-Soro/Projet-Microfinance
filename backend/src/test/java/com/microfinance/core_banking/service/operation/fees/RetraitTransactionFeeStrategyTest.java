package com.microfinance.core_banking.service.operation.fees;

import com.microfinance.core_banking.service.tarification.TarificationParametreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetraitTransactionFeeStrategyTest {

    @Mock
    private TarificationParametreService tarificationParametreService;

    @Test
    void shouldComputeFeeFromConfiguredRate() {
        RetraitTransactionFeeStrategy strategy = new RetraitTransactionFeeStrategy(tarificationParametreService);
        when(tarificationParametreService.lireValeurDecimale("TRANSACTION_FEE_RATE_RETRAIT"))
                .thenReturn(new BigDecimal("0.01"));

        BigDecimal frais = strategy.calculerFrais(new BigDecimal("1234.56"));

        assertEquals(0, new BigDecimal("12.35").compareTo(frais));
    }

    @Test
    void shouldFailWhenRateIsAboveOne() {
        RetraitTransactionFeeStrategy strategy = new RetraitTransactionFeeStrategy(tarificationParametreService);
        when(tarificationParametreService.lireValeurDecimale("TRANSACTION_FEE_RATE_RETRAIT"))
                .thenReturn(new BigDecimal("1.20"));

        assertThrows(IllegalStateException.class, () -> strategy.calculerFrais(new BigDecimal("100.00")));
    }
}
