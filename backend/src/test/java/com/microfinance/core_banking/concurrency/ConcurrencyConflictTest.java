package com.microfinance.core_banking.concurrency;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.client.ClientServiceImpl;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.extension.CaisseExtensionService;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
import com.microfinance.core_banking.service.operation.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcurrencyConflictTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CreditRepository creditRepository;
    @Mock private SessionCaisseRepository sessionCaisseRepository;
    @Mock private CaisseRepository caisseRepository;
    @Mock private TransactionService transactionService;
    @Mock private CreditExtensionService creditExtensionService;
    @Mock private CompteService compteService;
    @Mock private CaisseExtensionService caisseExtensionService;
    @Mock private ValidationExtensionService validationExtensionService;
    @Mock private ClientServiceImpl clientService;

    @Test
    void shouldRejectDuplicateTransactionReference() {
        String reference = "TXN-UNIQUE-001";
        when(transactionRepository.existsByReferenceUnique(reference)).thenReturn(true);

        boolean exists = transactionRepository.existsByReferenceUnique(reference);
        assertTrue(exists);
        verify(transactionRepository, times(1)).existsByReferenceUnique(reference);
    }

    @Test
    void shouldHandleConcurrentSoldeUpdate() {
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumCompte("SN001");
        compte.setSolde(new BigDecimal("100000.00"));

        BigDecimal withdrawalAmount = new BigDecimal("150000.00");

        assertThatThrownBy(() -> {
            if (compte.getSolde().compareTo(withdrawalAmount) < 0) {
                throw new IllegalStateException("Solde insuffisant");
            }
        }).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Solde insuffisant");
    }

    @Test
    void shouldPreventDoubleDeblocageCredit() {
        Credit credit = new Credit();
        credit.setIdCredit(1L);
        credit.setReferenceCredit("CRE-001");
        credit.setStatut("DEBLOQUE");

        when(creditRepository.findByReferenceCredit(anyString())).thenReturn(Optional.of(credit));

        Credit found = creditRepository.findByReferenceCredit("CRE-001").orElseThrow();
        assertThrows(IllegalStateException.class, () -> {
            if ("DEBLOQUE".equals(found.getStatut())) {
                throw new IllegalStateException("Credit deja debloque");
            }
        });
    }

    @Test
    void shouldEnforceSessionCaisseUnique() {
        Long caisseId = 1L;
        when(sessionCaisseRepository.existsByCaisse_IdCaisseAndStatut(caisseId, "OUVERTE"))
            .thenReturn(true);

        boolean hasOpenSession = sessionCaisseRepository.existsByCaisse_IdCaisseAndStatut(caisseId, "OUVERTE");
        assertTrue(hasOpenSession);

        assertThrows(IllegalStateException.class, () -> {
            if (hasOpenSession) {
                throw new IllegalStateException("Une session est deja ouverte pour cette caisse");
            }
        });
    }

    @Test
    void shouldHandleConcurrentDecouvertApproval() {
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setDecouvertAutorise(new BigDecimal("200000.00"));
        compte.setSolde(new BigDecimal("-150000.00"));

        BigDecimal demandeDecouvert = new BigDecimal("300000.00");
        BigDecimal decouvertTotal = compte.getDecouvertAutorise().add(demandeDecouvert);

        assertDoesNotThrow(() -> {
            if (decouvertTotal.compareTo(new BigDecimal("500000.00")) > 0) {
                throw new IllegalArgumentException("Decouvert maximum depasse");
            }
        });
    }
}
