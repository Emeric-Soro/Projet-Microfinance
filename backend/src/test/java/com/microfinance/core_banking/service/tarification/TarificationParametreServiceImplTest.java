package com.microfinance.core_banking.service.tarification;

import com.microfinance.core_banking.entity.TarificationParametre;
import com.microfinance.core_banking.repository.tarification.TarificationParametreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarificationParametreServiceImplTest {

    @Mock
    private TarificationParametreRepository repository;

    @Test
    void shouldReturnDecimalValueWhenParameterExists() {
        TarificationParametreServiceImpl service = new TarificationParametreServiceImpl(repository);
        TarificationParametre parametre = new TarificationParametre();
        parametre.setCleParametre("TRANSACTION_FEE_RATE_RETRAIT");
        parametre.setValeurParametre("0.01");
        when(repository.findByCleParametre("TRANSACTION_FEE_RATE_RETRAIT")).thenReturn(Optional.of(parametre));

        BigDecimal valeur = service.lireValeurDecimale("TRANSACTION_FEE_RATE_RETRAIT");

        assertEquals(0, new BigDecimal("0.01").compareTo(valeur));
    }

    @Test
    void shouldFailWhenParameterIsMissing() {
        TarificationParametreServiceImpl service = new TarificationParametreServiceImpl(repository);
        when(repository.findByCleParametre("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.lireValeurDecimale("UNKNOWN"));
    }
}
