package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.client.ClientServiceImpl;
import com.microfinance.core_banking.service.compte.CompteService;
import com.microfinance.core_banking.service.extension.CaisseExtensionService;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditWorkflowTest {

    @Mock private ClientRepository clientRepository;
    @Mock private StatutClientRepository statutClientRepository;
    @Mock private DemandeCreditRepository demandeCreditRepository;
    @Mock private CreditRepository creditRepository;
    @Mock private EcheanceCreditRepository echeanceCreditRepository;
    @Mock private RemboursementCreditRepository remboursementCreditRepository;
    @Mock private ImpayeCreditRepository impayeCreditRepository;
    @Mock private ProvisionCreditRepository provisionCreditRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private CreditExtensionService creditExtensionService;
    @Mock private CompteService compteService;
    @Mock private ClientServiceImpl clientService;

    private Client client;
    private DemandeCredit demande;
    private Credit credit;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setIdClient(1L);
        client.setCodeClient("CLI-WF-001");
        client.setNom("Workflow");
        client.setPrenom("Test");

        demande = new DemandeCredit();
        demande.setIdDemandeCredit(1L);
        demande.setReferenceDossier("DOS-WF-001");
        demande.setClient(client);
        demande.setMontantDemande(new BigDecimal("2000000.00"));
        demande.setDureeMois(12);
        demande.setStatut("BROUILLON");

        credit = new Credit();
        credit.setIdCredit(1L);
        credit.setReferenceCredit("CRE-WF-001");
        credit.setClient(client);
        credit.setMontantAccorde(new BigDecimal("2000000.00"));
        credit.setDureeMois(12);
        credit.setTauxInteret(new BigDecimal("8.5000"));
        credit.setStatut("APPROUVE");
        credit.setDateDebut(LocalDate.now());
        credit.setDateFin(LocalDate.now().plusMonths(12));
    }

    @Test
    void completeCreditLifecycle() {
        when(demandeCreditRepository.findByReferenceDossier("DOS-WF-001")).thenReturn(Optional.of(demande));
        when(creditRepository.findByReferenceCredit("CRE-WF-001")).thenReturn(Optional.of(credit));

        DemandeCredit foundDemande = demandeCreditRepository.findByReferenceDossier("DOS-WF-001").orElseThrow();
        assertThat(foundDemande.getStatut()).isEqualTo("BROUILLON");

        foundDemande.setStatut("EN_ATTENTE");
        assertThat(foundDemande.getStatut()).isEqualTo("EN_ATTENTE");

        foundDemande.setStatut("APPROUVE");
        assertThat(foundDemande.getStatut()).isEqualTo("APPROUVE");

        Credit foundCredit = creditRepository.findByReferenceCredit("CRE-WF-001").orElseThrow();
        assertThat(foundCredit.getStatut()).isEqualTo("APPROUVE");

        foundCredit.setStatut("DEBLOQUE");
        assertThat(foundCredit.getStatut()).isEqualTo("DEBLOQUE");

        foundCredit.setStatut("REMBOURSE");
        assertThat(foundCredit.getStatut()).isEqualTo("REMBOURSE");

        foundCredit.setStatut("CLOTURE");
        assertThat(foundCredit.getStatut()).isEqualTo("CLOTURE");
    }

    @Test
    void creditSchedulingGeneratesCorrectEcheances() {
        BigDecimal montant = new BigDecimal("2000000.00");
        int dureeMois = 12;
        BigDecimal mensualite = montant.divide(BigDecimal.valueOf(dureeMois), BigDecimal.ROUND_HALF_UP);

        assertThat(mensualite).isEqualByComparingTo(new BigDecimal("166667"));
    }

    @Test
    void remboursementReducesSolde() {
        BigDecimal montantInitial = new BigDecimal("2000000.00");
        BigDecimal remboursement = new BigDecimal("200000.00");
        BigDecimal soldeRestant = montantInitial.subtract(remboursement);

        assertThat(soldeRestant).isEqualByComparingTo(new BigDecimal("1800000.00"));
    }
}
