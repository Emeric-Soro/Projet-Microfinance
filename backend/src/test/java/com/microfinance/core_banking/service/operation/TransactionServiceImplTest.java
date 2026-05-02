package com.microfinance.core_banking.service.operation;

import com.microfinance.core_banking.config.TransactionWorkflowProperties;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeTransaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import com.microfinance.core_banking.repository.operation.LigneEcritureRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.operation.TypeTransactionRepository;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.service.communication.event.VirementEffectueEvent;
import com.microfinance.core_banking.service.operation.fees.TransactionFeeCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
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
    private SessionCaisseRepository sessionCaisseRepository;

    @Mock
    private LigneEcritureComptableRepository ligneEcritureComptableRepository;

    @Mock
    private TransactionFeeCalculator transactionFeeCalculator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransactionWorkflowProperties transactionWorkflowProperties;
    @Mock
    private ConformiteExtensionService conformiteExtensionService;
    @Mock
    private ComptabiliteExtensionService comptabiliteExtensionService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void shouldKeepSensitiveTransferPendingUntilSupervisorApproval() {
        Compte source = buildCompte(1L, "CPT-SRC", new BigDecimal("1000000.00"));
        Compte destination = buildCompte(2L, "CPT-DST", new BigDecimal("10000.00"));
        Utilisateur guichetier = buildUtilisateur(10L, "GUICHETIER");
        TypeTransaction typeTransaction = buildType("VIREMENT");

        when(compteRepository.findByNumCompte("CPT-SRC")).thenReturn(Optional.of(source));
        when(compteRepository.findByNumCompte("CPT-DST")).thenReturn(Optional.of(destination));
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
        when(comptabiliteExtensionService.calculerSoldeComptable("CPT-SRC")).thenReturn(new BigDecimal("1000000.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.approuverTransaction("TX-REF-001", 20L);

        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
        assertThat(resultat.getUtilisateurValidation()).isEqualTo(superviseur);
        assertThat(resultat.getDateValidation()).isNotNull();
        assertThat(resultat.getDateExecution()).isNotNull();
        verify(ligneEcritureRepository, times(2)).save(ArgumentMatchers.any());
        verify(comptabiliteExtensionService).comptabiliserTransaction(ArgumentMatchers.any(Transaction.class));
        verify(eventPublisher).publishEvent(ArgumentMatchers.any(VirementEffectueEvent.class));
    }

    @Test
    void shouldAllowApprovalWhenValidatorHasDecisionPermissionWithoutSupervisionRole() {
        Compte sourceStocke = buildCompte(1L, "CPT-SRC", new BigDecimal("1000000.00"));
        Compte destinationStocke = buildCompte(2L, "CPT-DST", new BigDecimal("10000.00"));
        Utilisateur initiateur = buildUtilisateur(10L, "GUICHETIER");
        Utilisateur validateur = buildUtilisateurWithPermission(20L, "ANALYSTE", "VALIDATION_DECIDE");
        TypeTransaction typeTransaction = buildType("VIREMENT");

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-REF-002");
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(new BigDecimal("100000.00"));
        transaction.setFrais(BigDecimal.ZERO);
        transaction.setUtilisateur(initiateur);
        transaction.setTypeTransaction(typeTransaction);
        transaction.setCompteSource(sourceStocke);
        transaction.setCompteDestination(destinationStocke);
        transaction.setStatutOperation(StatutOperation.EN_ATTENTE);
        transaction.setValidationSuperviseurRequise(true);

        when(transactionRepository.findByReferenceUnique("TX-REF-002")).thenReturn(Optional.of(transaction));
        when(utilisateurRepository.findById(20L)).thenReturn(Optional.of(validateur));
        when(compteRepository.findById(1L)).thenReturn(Optional.of(sourceStocke));
        when(compteRepository.findById(2L)).thenReturn(Optional.of(destinationStocke));
        when(comptabiliteExtensionService.calculerSoldeComptable("CPT-SRC")).thenReturn(new BigDecimal("1000000.00"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.approuverTransaction("TX-REF-002", 20L);

        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
        assertThat(resultat.getUtilisateurValidation()).isEqualTo(validateur);
    }

    @Test
    void shouldCancelPendingTransactionUsingSupervisorWorkflow() {
        Utilisateur initiateur = buildUtilisateur(10L, "GUICHETIER");
        Utilisateur superviseur = buildUtilisateur(20L, "SUPERVISEUR");
        TypeTransaction typeTransaction = buildType("DEPOT");

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-PENDING-01");
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(new BigDecimal("50000.00"));
        transaction.setFrais(BigDecimal.ZERO);
        transaction.setUtilisateur(initiateur);
        transaction.setTypeTransaction(typeTransaction);
        transaction.setStatutOperation(StatutOperation.EN_ATTENTE);
        transaction.setValidationSuperviseurRequise(true);

        when(transactionRepository.findByReferenceUnique("TX-PENDING-01")).thenReturn(Optional.of(transaction));
        when(utilisateurRepository.findById(20L)).thenReturn(Optional.of(superviseur));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.annulerTransaction("TX-PENDING-01", 20L, "annulation");

        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.REJETEE);
        assertThat(resultat.getMotifRejet()).isEqualTo("annulation");
    }

    @Test
    void shouldCreateReversalTransactionForExecutedDeposit() {
        Compte destination = buildCompte(2L, "CPT-DST", new BigDecimal("10000.00"));
        Utilisateur initiateur = buildUtilisateur(10L, "GUICHETIER");
        Utilisateur superviseur = buildUtilisateur(20L, "SUPERVISEUR");
        TypeTransaction typeDepot = buildType("DEPOT");
        TypeTransaction typeRetrait = buildType("RETRAIT");

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("TX-EXEC-01");
        transaction.setDateHeureTransaction(LocalDateTime.now());
        transaction.setMontantGlobal(new BigDecimal("50000.00"));
        transaction.setFrais(BigDecimal.ZERO);
        transaction.setUtilisateur(initiateur);
        transaction.setTypeTransaction(typeDepot);
        transaction.setCompteDestination(destination);
        transaction.setCodeOperationMetier("DEPOT_CASH");
        transaction.setStatutOperation(StatutOperation.EXECUTEE);

        when(transactionRepository.findByReferenceUnique("TX-EXEC-01")).thenReturn(Optional.of(transaction));
        when(transactionRepository.existsByReferenceUnique("EXT-TX-EXEC-01")).thenReturn(false);
        when(utilisateurRepository.findById(20L)).thenReturn(Optional.of(superviseur));
        when(compteRepository.findByNumCompte("CPT-DST")).thenReturn(Optional.of(destination));
        when(compteRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(comptabiliteExtensionService.calculerSoldeComptable("CPT-DST")).thenReturn(new BigDecimal("90000.00"));
        when(typeTransactionRepository.findByCodeTypeTransaction("RETRAIT")).thenReturn(Optional.of(typeRetrait));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.extournerTransaction("TX-EXEC-01", 20L, "extourne");

        assertThat(resultat.getReferenceUnique()).isEqualTo("EXT-TX-EXEC-01");
        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
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

    private Utilisateur buildUtilisateurWithPermission(Long idUser, String roleCode, String permissionCode) {
        PermissionSecurite permission = new PermissionSecurite();
        permission.setCodePermission(permissionCode);
        permission.setActif(true);

        RoleUtilisateur roleUtilisateur = new RoleUtilisateur();
        roleUtilisateur.setCodeRoleUtilisateur(roleCode);
        roleUtilisateur.setPermissions(Set.of(permission));

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

    private LigneEcritureComptable ligneComptable(String sens, String montant) {
        LigneEcritureComptable ligne = new LigneEcritureComptable();
        ligne.setSens(sens);
        ligne.setMontant(new BigDecimal(montant));
        return ligne;
    }
}
