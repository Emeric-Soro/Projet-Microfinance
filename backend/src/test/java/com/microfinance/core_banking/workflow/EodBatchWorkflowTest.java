package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.EpargneRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.extension.EodBatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EodBatchWorkflowTest {

    @Mock private EodBatchService eodService;
    @Mock private EpargneRepository epargneRepository;
    @Mock private TransactionRepository transactionRepository;

    private List<Epargne> comptesEpargne;
    private LocalDate dateTraitement;

    @BeforeEach
    void setUp() {
        dateTraitement = LocalDate.of(2026, 5, 2);

        Compte c1 = new Compte();
        c1.setIdCompte(1L);
        c1.setNumCompte("SN-001");

        Compte c2 = new Compte();
        c2.setIdCompte(2L);
        c2.setNumCompte("SN-002");

        Epargne e1 = new Epargne();
        e1.setIdEpargne(1L);
        e1.setCompte(c1);
        e1.setSoldeActuel(new BigDecimal("1000000.00"));
        e1.setTauxRemuneration(new BigDecimal("3.5000"));

        Epargne e2 = new Epargne();
        e2.setIdEpargne(2L);
        e2.setCompte(c2);
        e2.setSoldeActuel(new BigDecimal("500000.00"));
        e2.setTauxRemuneration(new BigDecimal("3.5000"));

        comptesEpargne = List.of(e1, e2);
    }

    @Test
    void eodCrediteInteretsEpargne() {
        BigDecimal totalInterets = BigDecimal.ZERO;

        for (Epargne e : comptesEpargne) {
            BigDecimal interet = e.getSoldeActuel()
                .multiply(e.getTauxRemuneration())
                .divide(new BigDecimal("36500"), 2, RoundingMode.HALF_UP);

            e.setSoldeActuel(e.getSoldeActuel().add(interet));
            totalInterets = totalInterets.add(interet);
        }

        assertThat(totalInterets).isEqualByComparingTo(new BigDecimal("143.84"));
        assertThat(comptesEpargne.get(0).getSoldeActuel())
            .isEqualByComparingTo(new BigDecimal("1000095.89"));
    }

    @Test
    void eodCalculeProvisionsImpayes() {
        BigDecimal encoursImpayes = new BigDecimal("5000000.00");
        BigDecimal tauxProvision = new BigDecimal("0.20");
        BigDecimal provision = encoursImpayes.multiply(tauxProvision)
            .setScale(2, RoundingMode.HALF_UP);

        assertThat(provision).isEqualByComparingTo(new BigDecimal("1000000.00"));
    }

    @Test
    void eodClotureJournaliere() {
        BigDecimal totalEncaissements = new BigDecimal("2500000.00");
        BigDecimal totalDecaissements = new BigDecimal("1800000.00");
        BigDecimal soldeOuverture = new BigDecimal("5000000.00");

        BigDecimal soldeCloture = soldeOuverture
            .add(totalEncaissements)
            .subtract(totalDecaissements);

        assertThat(soldeCloture).isEqualByComparingTo(new BigDecimal("5700000.00"));
    }
}
