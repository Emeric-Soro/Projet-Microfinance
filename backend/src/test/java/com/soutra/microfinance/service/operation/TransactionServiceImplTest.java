package com.soutra.microfinance.service.operation;

import com.soutra.microfinance.config.TransactionWorkflowProperties;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import com.soutra.microfinance.repository.operation.TypeTransactionRepository;
import com.soutra.microfinance.repository.compte.CarteVisaRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import com.soutra.microfinance.service.comptabilite.ComptabiliteOperationnelleService;
import com.soutra.microfinance.service.communication.event.VirementEffectueEvent;
import com.soutra.microfinance.service.operation.fees.TransactionFeeCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.soutra.microfinance.constant.AppConstants;
import com.soutra.microfinance.api.exception.TransactionWorkflowException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LigneEcritureRepository ligneEcritureRepository;

    @Mock
    private TypeTransactionRepository typeTransactionRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private TransactionFeeCalculator transactionFeeCalculator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransactionWorkflowProperties transactionWorkflowProperties;

    @Mock
    private CarteVisaRepository carteVisaRepository;

    @Mock
    private CaisseRepository caisseRepository;

    @Mock
    private ComptabiliteOperationnelleService comptabiliteOperationnelleService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().doAnswer(invocation -> {
            Compte compte = invocation.getArgument(0);
            BigDecimal amount = invocation.getArgument(1);
            compte.setSolde(compte.getSolde().add(amount));
            return null;
        }).when(comptabiliteOperationnelleService).crediterCompte(any(Compte.class), any(BigDecimal.class));

        org.mockito.Mockito.lenient().doAnswer(invocation -> {
            Compte compte = invocation.getArgument(0);
            BigDecimal amount = invocation.getArgument(1);
            compte.setSolde(compte.getSolde().subtract(amount));
            return null;
        }).when(comptabiliteOperationnelleService).debiterCompte(any(Compte.class), any(BigDecimal.class));
    }

    @Test
    void shouldKeepSensitiveTransferPendingUntilSupervisorApproval() {
        Compte source = buildCompte(1L, "CPT-SRC", new BigDecimal("1000000.00"));
        Compte destination = buildCompte(2L, "CPT-DST", new BigDecimal("10000.00"));
        Utilisateur guichetier = buildUtilisateur(10L, "GUICHETIER");
        TypeTransaction typeTransaction = buildType("VIREMENT");

        when(compteRepository.findByNumCompte("CPT-SRC")).thenReturn(Optional.of(source));
        when(compteRepository.findByNumCompte("CPT-DST")).thenReturn(Optional.of(destination));
        when(compteRepository.findById(1L)).thenReturn(Optional.of(source));
        when(compteRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(guichetier));
        when(typeTransactionRepository.findByCodeTypeTransaction("VIREMENT")).thenReturn(Optional.of(typeTransaction));
        when(transactionFeeCalculator.calculerFrais("VIREMENT", new BigDecimal("600000.00"))).thenReturn(BigDecimal.ZERO);
        when(transactionWorkflowProperties.getApprovalThreshold()).thenReturn(new BigDecimal("500000.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.faireVirement("CPT-SRC", "CPT-DST", new BigDecimal("600000.00"), 10L);

        assertThat(transaction.getStatutOperation()).isEqualTo(StatutOperation.EN_ATTENTE);
        assertThat(transaction.getValidationSuperviseurRequise()).isTrue();
        verify(compteRepository, never()).save(any(Compte.class));
        verify(ligneEcritureRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldExecutePendingTransferOnSupervisorApproval() {
        Compte sourceStocke = buildCompte(1L, "CPT-SRC", new BigDecimal("1000000.00"));
        Compte destinationStocke = buildCompte(2L, "CPT-DST", new BigDecimal("10000.00"));
        Utilisateur initiateur = buildUtilisateur(10L, "GUICHETIER");
        Utilisateur superviseur = buildUtilisateur(20L, "SUPERVISEUR");
        TypeTransaction typeTransaction = buildType("VIREMENT");

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-REF-001");
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(new BigDecimal("600000.00"));
        transaction.setFrais(BigDecimal.ZERO);
        transaction.setUtilisateur(initiateur);
        transaction.setTypeTransaction(typeTransaction);
        transaction.setCompteSource(sourceStocke);
        transaction.setCompteDestination(destinationStocke);
        transaction.setStatutOperation(StatutOperation.EN_ATTENTE);
        transaction.setValidationSuperviseurRequise(true);

        when(transactionRepository.findByReferenceUnique("TX-REF-001")).thenReturn(Optional.of(transaction));
        when(utilisateurRepository.findById(20L)).thenReturn(Optional.of(superviseur));
        when(compteRepository.findById(1L)).thenReturn(Optional.of(sourceStocke));
        when(compteRepository.findById(2L)).thenReturn(Optional.of(destinationStocke));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.approuverTransaction("TX-REF-001", 20L);

        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
        assertThat(resultat.getUtilisateurValidation()).isEqualTo(superviseur);
        assertThat(resultat.getDateValidation()).isNotNull();
        assertThat(resultat.getDateExecution()).isNotNull();
        assertThat(sourceStocke.getSolde()).isEqualByComparingTo("400000.00");
        assertThat(destinationStocke.getSolde()).isEqualByComparingTo("610000.00");
        verify(comptabiliteOperationnelleService, times(2)).creerLigne(any(), any(), any(), any());
        verify(eventPublisher).publishEvent(ArgumentMatchers.any(VirementEffectueEvent.class));
    }

    @Test
    void shouldPerformInitialDepositSuccessfully() {
        Compte compte = buildCompte(1L, "CPT-DEP-INIT", BigDecimal.ZERO);
        Utilisateur agent = buildUtilisateur(10L, "AGENT_COMMERCIAL");
        TypeTransaction typeDepot = buildType("DEPOT");

        when(compteRepository.findByNumCompte("CPT-DEP-INIT")).thenReturn(Optional.of(compte));
        when(compteRepository.findById(1L)).thenReturn(Optional.of(compte));
        when(utilisateurRepository.findById(10L)).thenReturn(Optional.of(agent));
        when(typeTransactionRepository.findByCodeTypeTransaction(AppConstants.TX_DEPOT)).thenReturn(Optional.of(typeDepot));
        when(transactionFeeCalculator.calculerFrais("DEPOT", new BigDecimal("10000.00"))).thenReturn(BigDecimal.ZERO);
        when(transactionWorkflowProperties.getApprovalThreshold()).thenReturn(new BigDecimal("50000.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.faireDepotInitial("CPT-DEP-INIT", new BigDecimal("10000.00"), 10L);

        assertThat(result.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
        assertThat(result.getValidationSuperviseurRequise()).isFalse();
        assertThat(compte.getSolde()).isEqualByComparingTo("10000.00");
        verify(comptabiliteOperationnelleService, times(1)).crediterCompte(eq(compte), eq(new BigDecimal("10000.00")));
    }

    @Test
    void shouldFailInitialDepositWhenAccountAlreadyHasBalance() {
        Compte compte = buildCompte(1L, "CPT-DEP-INIT", new BigDecimal("5000.00"));
        Utilisateur agent = buildUtilisateur(10L, "AGENT_COMMERCIAL");

        when(compteRepository.findByNumCompte("CPT-DEP-INIT")).thenReturn(Optional.of(compte));

        assertThatThrownBy(() -> transactionService.faireDepotInitial("CPT-DEP-INIT", new BigDecimal("10000.00"), 10L))
                .isInstanceOf(TransactionWorkflowException.class)
                .hasMessageContaining("Le depot initial n'est autorise que sur un compte dont le solde est nul.");

        verify(transactionRepository, never()).save(any());
        verify(compteRepository, never()).save(any());
    }

    private Compte buildCompte(Long idCompte, String numCompte, BigDecimal solde) {
        Compte compte = new Compte();
        compte.setIdCompte(idCompte);
        compte.setNumCompte(numCompte);
        compte.setSolde(solde);
        compte.setDecouvertAutorise(BigDecimal.ZERO);
        return compte;
    }

    private Utilisateur buildUtilisateur(Long idUser, String roleCode) {
        RoleUtilisateur roleUtilisateur = new RoleUtilisateur();
        roleUtilisateur.setCodeRoleUtilisateur(roleCode);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(idUser);
        utilisateur.setRoles(Set.of(roleUtilisateur));
        return utilisateur;
    }

    private TypeTransaction buildType(String code) {
        TypeTransaction typeTransaction = new TypeTransaction();
        typeTransaction.setCodeTypeTransaction(code);
        typeTransaction.setLibelle(code);
        return typeTransaction;
    }
}
