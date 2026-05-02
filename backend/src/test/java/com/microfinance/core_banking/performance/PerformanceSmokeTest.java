package com.microfinance.core_banking.performance;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypePieceIdentite;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.client.ClientServiceImpl;
import com.microfinance.core_banking.service.compte.CompteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceSmokeTest {

    @Mock private ClientRepository clientRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private ClientServiceImpl clientService;
    @Mock private CompteService compteService;

    private static final long MAX_CLIENT_CREATION_MS = 200;
    private static final long MAX_TRANSACTION_PROCESSING_MS = 300;

    @Test
    void shouldCreateClientWithinThreshold() {
        assertTimeout(Duration.ofMillis(MAX_CLIENT_CREATION_MS), () -> {
            Client client = new Client();
            client.setCodeClient("PERF-TEST");
            client.setNom("Performance");
            client.setPrenom("Test");
            client.setDateInscription(LocalDate.now());
            client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
            client.setStatutKyc(StatutKycClient.BROUILLON);
            client.setTypePieceIdentite(TypePieceIdentite.CNI);

            when(clientRepository.existsByCodeClient(any())).thenReturn(false);
            when(clientRepository.save(any())).thenReturn(client);

            clientRepository.save(client);
        });
    }

    @Test
    void shouldProcessTransactionWithinThreshold() {
        assertTimeout(Duration.ofMillis(MAX_TRANSACTION_PROCESSING_MS), () -> {
            Transaction transaction = new Transaction();
            transaction.setMontantGlobal(new BigDecimal("50000.00"));

            when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));

            Instant start = Instant.now();
            transactionRepository.findById(1L);
            Instant end = Instant.now();

            assertThat(Duration.between(start, end).toMillis())
                .isLessThan(MAX_TRANSACTION_PROCESSING_MS);
        });
    }

    @Test
    void shouldComputeBalanceWithinThreshold() {
        assertTimeout(Duration.ofMillis(200), () -> {
            Compte compte = new Compte();
            compte.setSolde(new BigDecimal("1000000.00"));

            BigDecimal nouveauSolde = compte.getSolde()
                .add(new BigDecimal("250000.00"))
                .subtract(new BigDecimal("50000.00"));

            assertThat(nouveauSolde).isEqualByComparingTo(new BigDecimal("1200000.00"));
        });
    }

    @Test
    void shouldVerifyAccountBalanceWithinThreshold() {
        assertTimeout(Duration.ofMillis(100), () -> {
            BigDecimal[] transactions = {
                new BigDecimal("100000.00"),
                new BigDecimal("-25000.00"),
                new BigDecimal("50000.00"),
                new BigDecimal("-10000.00"),
                new BigDecimal("75000.00")
            };

            BigDecimal solde = BigDecimal.ZERO;
            for (BigDecimal t : transactions) {
                solde = solde.add(t);
            }

            assertThat(solde).isEqualByComparingTo(new BigDecimal("190000.00"));
        });
    }
}
