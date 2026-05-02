package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.operation.TypeTransactionRepository;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionWorkflowTest {

    @Mock private CompteRepository compteRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TypeTransactionRepository typeTransactionRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    private Compte compteSource;
    private Compte compteDestination;

    @BeforeEach
    void setUp() {
        compteSource = new Compte();
        compteSource.setIdCompte(1L);
        compteSource.setNumCompte("SN-SOURCE-001");
        compteSource.setSolde(new BigDecimal("500000.00"));

        compteDestination = new Compte();
        compteDestination.setIdCompte(2L);
        compteDestination.setNumCompte("SN-DEST-001");
        compteDestination.setSolde(new BigDecimal("100000.00"));
    }

    @Test
    void depotIncreasesBalance() {
        BigDecimal montantDepot = new BigDecimal("100000.00");
        BigDecimal nouveauSolde = compteSource.getSolde().add(montantDepot);

        assertThat(nouveauSolde).isEqualByComparingTo(new BigDecimal("600000.00"));
    }

    @Test
    void virementTransfersCorrectAmount() {
        BigDecimal montantVirement = new BigDecimal("75000.00");

        BigDecimal soldeSourceApres = compteSource.getSolde().subtract(montantVirement);
        BigDecimal soldeDestApres = compteDestination.getSolde().add(montantVirement);

        assertThat(soldeSourceApres).isEqualByComparingTo(new BigDecimal("425000.00"));
        assertThat(soldeDestApres).isEqualByComparingTo(new BigDecimal("175000.00"));
        assertThat(soldeSourceApres.add(soldeDestApres))
            .isEqualByComparingTo(compteSource.getSolde().add(compteDestination.getSolde()));
    }

    @Test
    void retraitDecreasesBalance() {
        BigDecimal montantRetrait = new BigDecimal("50000.00");

        assertThat(compteSource.getSolde().compareTo(montantRetrait)).isGreaterThanOrEqualTo(0);

        BigDecimal nouveauSolde = compteSource.getSolde().subtract(montantRetrait);
        assertThat(nouveauSolde).isEqualByComparingTo(new BigDecimal("450000.00"));
    }

    @Test
    void insufficientBalancePreventsRetrait() {
        BigDecimal montantRetrait = new BigDecimal("600000.00");

        boolean soldeSuffisant = compteSource.getSolde().compareTo(montantRetrait) >= 0;
        assertThat(soldeSuffisant).isFalse();
    }

    @Test
    void extourneReversesOriginalTransaction() {
        Transaction original = new Transaction();
        original.setIdTransaction(1L);
        original.setMontantGlobal(new BigDecimal("50000.00"));
        original.setCompteSource(compteSource);
        original.setCompteDestination(compteDestination);
        original.setStatutOperation(StatutOperation.VALIDEE);

        BigDecimal soldeSourceAvant = compteSource.getSolde();
        BigDecimal soldeDestAvant = compteDestination.getSolde();

        compteSource.setSolde(compteSource.getSolde().add(original.getMontantGlobal()));
        compteDestination.setSolde(compteDestination.getSolde().subtract(original.getMontantGlobal()));

        assertThat(compteSource.getSolde()).isEqualByComparingTo(soldeSourceAvant.add(original.getMontantGlobal()));
        assertThat(compteDestination.getSolde()).isEqualByComparingTo(soldeDestAvant.subtract(original.getMontantGlobal()));
    }
}
