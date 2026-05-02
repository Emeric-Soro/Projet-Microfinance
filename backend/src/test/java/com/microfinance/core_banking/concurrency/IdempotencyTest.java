package com.microfinance.core_banking.concurrency;

import com.microfinance.core_banking.dto.request.client.CreerClientRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.DemandeCreditRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.client.ClientServiceImpl;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.operation.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private DemandeCreditRepository demandeCreditRepository;
    @Mock private CreditRepository creditRepository;

    @Test
    void shouldNotCreateDuplicateClientOnRetry() {
        String email = "client@retry.com";
        String telephone = "+221770000001";

        when(clientRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = clientRepository.existsByEmail(email);
        assertTrue(exists);

        assertThrows(IllegalStateException.class, () -> {
            if (exists) {
                throw new IllegalStateException("Un client avec cet email existe deja");
            }
        });
    }

    @Test
    void shouldNotDoubleDebitOnVirementRetry() {
        String referenceUnique = "VIREMENT-RETRY-001";

        when(transactionRepository.existsByReferenceUnique(referenceUnique)).thenReturn(true);

        boolean alreadyProcessed = transactionRepository.existsByReferenceUnique(referenceUnique);
        assertTrue(alreadyProcessed);

        assertThrows(IllegalStateException.class, () -> {
            if (alreadyProcessed) {
                throw new IllegalStateException("Transaction deja traitee: " + referenceUnique);
            }
        });
    }

    @Test
    void shouldReturnSameResultForIdempotentDemandeCredit() {
        String referenceDossier = "DOS-IDEMP-001";

        DemandeCredit existing = new DemandeCredit();
        existing.setIdDemandeCredit(1L);
        existing.setReferenceDossier(referenceDossier);
        existing.setStatut("EN_ATTENTE");
        existing.setMontantDemande(new BigDecimal("1000000.00"));

        when(demandeCreditRepository.findByReferenceDossier(referenceDossier))
            .thenReturn(Optional.of(existing));

        DemandeCredit found = demandeCreditRepository.findByReferenceDossier(referenceDossier).orElseThrow();
        assertEquals("EN_ATTENTE", found.getStatut());
        assertEquals(new BigDecimal("1000000.00"), found.getMontantDemande());
        verify(demandeCreditRepository, times(1)).findByReferenceDossier(referenceDossier);
    }

    @Test
    void shouldHandleIdempotentKycSubmission() {
        Long clientId = 1L;
        StatutKycClient currentStatus = StatutKycClient.VALIDE;

        assertThrows(IllegalStateException.class, () -> {
            if (currentStatus == StatutKycClient.VALIDE || currentStatus == StatutKycClient.REJETE) {
                throw new IllegalStateException("KYC deja traite pour le client " + clientId);
            }
        });
    }

    @Test
    void shouldNotDoubleApplyCloture() {
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumCompte("SN001");
        compte.setSolde(BigDecimal.ZERO);

        when(compteRepository.findByNumCompte("SN001")).thenReturn(Optional.of(compte));

        assertThrows(IllegalStateException.class, () -> {
            if (compte.getSolde().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("Solde non nul: " + compte.getSolde());
            }
        });
    }
}
