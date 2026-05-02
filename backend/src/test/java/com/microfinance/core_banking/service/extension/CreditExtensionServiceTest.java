package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CalculerProvisionsRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DebloquerCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.DetecterImpayesRequestDTO;
import com.microfinance.core_banking.dto.request.extension.PassagePerteCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ReportEcheanceCreditRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RestructurationCreditRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.EcheanceCredit;
import com.microfinance.core_banking.entity.ImpayeCredit;
import com.microfinance.core_banking.entity.ProduitCredit;
import com.microfinance.core_banking.entity.ProvisionCredit;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.DemandeCreditRepository;
import com.microfinance.core_banking.repository.extension.EcheanceCreditRepository;
import com.microfinance.core_banking.repository.extension.GarantieCreditRepository;
import com.microfinance.core_banking.repository.extension.ImpayeCreditRepository;
import com.microfinance.core_banking.repository.extension.ProduitCreditRepository;
import com.microfinance.core_banking.repository.extension.ProvisionCreditRepository;
import com.microfinance.core_banking.repository.extension.RemboursementCreditRepository;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditExtensionServiceTest {

    @Mock
    private ProduitCreditRepository produitCreditRepository;
    @Mock
    private DemandeCreditRepository demandeCreditRepository;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AgenceRepository agenceRepository;
    @Mock
    private EcheanceCreditRepository echeanceCreditRepository;
    @Mock
    private GarantieCreditRepository garantieCreditRepository;
    @Mock
    private RemboursementCreditRepository remboursementCreditRepository;
    @Mock
    private ImpayeCreditRepository impayeCreditRepository;
    @Mock
    private ProvisionCreditRepository provisionCreditRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private ComptabiliteExtensionService comptabiliteExtensionService;

    @InjectMocks
    private CreditExtensionService creditExtensionService;

    @Test
    void debloquerCredit_neReinjectePasLesFraisDansLesEcheances() {
        ProduitCredit produitCredit = new ProduitCredit();
        produitCredit.setFraisDossier(new BigDecimal("100"));
        produitCredit.setAssuranceTaux(new BigDecimal("2"));
        produitCredit.setTauxAnnuel(new BigDecimal("12"));

        Client client = new Client();
        client.setIdClient(77L);

        DemandeCredit demandeCredit = new DemandeCredit();
        demandeCredit.setIdDemandeCredit(5L);
        demandeCredit.setClient(client);
        demandeCredit.setProduitCredit(produitCredit);
        demandeCredit.setMontantDemande(new BigDecimal("10000"));
        demandeCredit.setDureeMois(12);
        demandeCredit.setStatut("APPROUVEE");

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-CREDIT-01");
        Utilisateur user = new Utilisateur();
        user.setIdUser(12L);

        when(demandeCreditRepository.findById(5L)).thenReturn(Optional.of(demandeCredit));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(user);
        when(transactionService.posterDepotSysteme(any(), any(), any(), any(), any(), any())).thenReturn(transaction);
        when(creditRepository.save(any(Credit.class))).thenAnswer(invocation -> {
            Credit credit = invocation.getArgument(0);
            if (credit.getIdCredit() == null) {
                credit.setIdCredit(20L);
            }
            return credit;
        });
        when(echeanceCreditRepository.save(any(EcheanceCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DebloquerCreditRequestDTO dto = new DebloquerCreditRequestDTO();
        dto.setNumCompteDestination("CPT-DEST");
        creditExtensionService.debloquerCredit(5L, dto);

        ArgumentCaptor<EcheanceCredit> captor = ArgumentCaptor.forClass(EcheanceCredit.class);
        verify(echeanceCreditRepository, org.mockito.Mockito.times(12)).save(captor.capture());
        assertThat(captor.getAllValues()).allSatisfy(echeance -> assertThat(echeance.getAssurancePrevue()).isEqualByComparingTo(BigDecimal.ZERO));
    }

    @Test
    void detecterImpayes_clotureUnImpayeRegularise() {
        Credit credit = new Credit();
        credit.setIdCredit(9L);
        credit.setClient(new Client());

        EcheanceCredit echeance = new EcheanceCredit();
        echeance.setIdEcheanceCredit(33L);
        echeance.setCredit(credit);
        echeance.setDateEcheance(LocalDate.now().minusDays(10));
        echeance.setTotalPrevu(new BigDecimal("1000"));
        echeance.setCapitalPaye(new BigDecimal("1000"));
        echeance.setInteretPaye(BigDecimal.ZERO);
        echeance.setAssurancePayee(BigDecimal.ZERO);
        echeance.setStatut("IMPAYEE");

        ImpayeCredit impaye = new ImpayeCredit();
        impaye.setIdImpayeCredit(4L);
        impaye.setStatut("OUVERT");

        when(creditRepository.findAll()).thenReturn(List.of(credit));
        when(echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(9L)).thenReturn(List.of(echeance));
        when(impayeCreditRepository.findByEcheanceCredit_IdEcheanceCredit(33L)).thenReturn(Optional.of(impaye));
        when(impayeCreditRepository.save(any(ImpayeCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(echeanceCreditRepository.save(any(EcheanceCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticatedUserService.hasGlobalScope()).thenReturn(true);

        DetecterImpayesRequestDTO dto = new DetecterImpayesRequestDTO();
        dto.setDateArrete(LocalDate.now());
        creditExtensionService.detecterImpayes(dto);

        assertThat(impaye.getStatut()).isEqualTo("CLOTURE");
        assertThat(echeance.getStatut()).isEqualTo("REGLEE");
    }

    @Test
    void calculerProvisions_reutiliseLaProvisionDeLaMemeDate() {
        Credit credit = new Credit();
        credit.setIdCredit(11L);
        credit.setCapitalRestantDu(new BigDecimal("5000"));
        credit.setClient(new Client());
        ProvisionCredit provisionExistante = new ProvisionCredit();
        provisionExistante.setIdProvisionCredit(8L);
        provisionExistante.setReferencePieceComptable(null);

        ImpayeCredit impaye = new ImpayeCredit();
        impaye.setJoursRetard(45);
        impaye.setStatut("OUVERT");

        when(creditRepository.findAll()).thenReturn(List.of(credit));
        when(echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(11L)).thenReturn(List.of());
        when(impayeCreditRepository.findByCredit_IdCreditAndStatutIgnoreCaseOrderByJoursRetardDesc(11L, "OUVERT")).thenReturn(List.of(impaye));
        when(provisionCreditRepository.findByCredit_IdCreditAndDateCalcul(11L, LocalDate.of(2026, 5, 1))).thenReturn(Optional.of(provisionExistante));
        when(provisionCreditRepository.save(any(ProvisionCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(authenticatedUserService.hasGlobalScope()).thenReturn(true);

        CalculerProvisionsRequestDTO dto = new CalculerProvisionsRequestDTO();
        dto.setDateCalcul(LocalDate.of(2026, 5, 1));
        List<ProvisionCredit> resultats = creditExtensionService.calculerProvisions(dto);

        assertThat(resultats).hasSize(1);
        assertThat(resultats.get(0).getIdProvisionCredit()).isEqualTo(8L);
        verify(comptabiliteExtensionService).comptabiliserProvisionCredit(provisionExistante);
    }

    @Test
    void restructurerCredit_regenereLesEcheancesFutures() {
        Client client = new Client();
        client.setIdClient(42L);

        Credit credit = new Credit();
        credit.setIdCredit(12L);
        credit.setClient(client);
        credit.setTauxAnnuel(new BigDecimal("12"));
        credit.setCapitalRestantDu(new BigDecimal("12000"));
        credit.setMensualite(new BigDecimal("1200"));
        credit.setDateDeblocage(LocalDateTime.now().minusMonths(3));
        credit.setDateProchaineEcheance(LocalDate.now().plusMonths(1));

        EcheanceCredit future = new EcheanceCredit();
        future.setNumeroEcheance(4);
        future.setCapitalPaye(BigDecimal.ZERO);
        future.setInteretPaye(BigDecimal.ZERO);
        future.setAssurancePayee(BigDecimal.ZERO);

        when(creditRepository.findById(12L)).thenReturn(Optional.of(credit));
        when(creditRepository.save(any(Credit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(echeanceCreditRepository.findByCredit_IdCreditOrderByNumeroEcheanceAsc(12L)).thenReturn(List.of(future));
        when(echeanceCreditRepository.save(any(EcheanceCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RestructurationCreditRequestDTO dto = new RestructurationCreditRequestDTO(12L, 6, new BigDecimal("10"), "amenagement");
        Credit resultat = creditExtensionService.restructurerCredit(12L, dto);

        assertThat(resultat.getStatut()).isEqualTo("RESTRUCTURE");
        verify(echeanceCreditRepository).delete(future);
    }

    @Test
    void passerEnPerte_refuseUnCreditSansRetardSuffisant() {
        Credit credit = new Credit();
        credit.setIdCredit(14L);
        credit.setClient(new Client());

        ImpayeCredit impaye = new ImpayeCredit();
        impaye.setJoursRetard(90);

        when(creditRepository.findById(14L)).thenReturn(Optional.of(credit));
        when(impayeCreditRepository.findByCredit_IdCreditAndStatutIgnoreCaseOrderByJoursRetardDesc(14L, "OUVERT")).thenReturn(List.of(impaye));

        assertThatThrownBy(() -> creditExtensionService.passerEnPerte(14L, new PassagePerteCreditRequestDTO(14L, "test")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("180 jours");
    }

    @Test
    void reporterEcheance_metAJourLaDateEtLeStatut() {
        Client client = new Client();
        client.setIdClient(55L);

        Credit credit = new Credit();
        credit.setIdCredit(15L);
        credit.setClient(client);

        EcheanceCredit echeance = new EcheanceCredit();
        echeance.setIdEcheanceCredit(99L);
        echeance.setCredit(credit);
        echeance.setDateEcheance(LocalDate.of(2026, 5, 10));
        echeance.setStatut("A_ECHOIR");

        when(creditRepository.findById(15L)).thenReturn(Optional.of(credit));
        when(echeanceCreditRepository.findById(99L)).thenReturn(Optional.of(echeance));
        when(echeanceCreditRepository.save(any(EcheanceCredit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(echeanceCreditRepository.findByCredit_IdCreditOrderByDateEcheanceAsc(15L)).thenReturn(List.of(echeance));
        when(creditRepository.save(any(Credit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportEcheanceCreditRequestDTO dto = new ReportEcheanceCreditRequestDTO(15L, 99L, LocalDate.of(2026, 6, 10), "report");
        EcheanceCredit resultat = creditExtensionService.reporterEcheance(15L, 99L, dto);

        assertThat(resultat.getStatut()).isEqualTo("REPORTEE");
        assertThat(resultat.getDateEcheance()).isEqualTo(LocalDate.of(2026, 6, 10));
    }
}
