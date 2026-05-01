package com.microfinance.core_banking.service.operation;

import com.microfinance.core_banking.config.TransactionWorkflowProperties;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeTransaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.LigneEcritureRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.operation.TypeTransactionRepository;
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
    private TransactionFeeCalculator transactionFeeCalculator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TransactionWorkflowProperties transactionWorkflowProperties;

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
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction resultat = transactionService.approuverTransaction("TX-REF-001", 20L);

        assertThat(resultat.getStatutOperation()).isEqualTo(StatutOperation.EXECUTEE);
        assertThat(resultat.getUtilisateurValidation()).isEqualTo(superviseur);
        assertThat(resultat.getDateValidation()).isNotNull();
        assertThat(resultat.getDateExecution()).isNotNull();
        assertThat(sourceStocke.getSolde()).isEqualByComparingTo("400000.00");
        assertThat(destinationStocke.getSolde()).isEqualByComparingTo("610000.00");
        verify(ligneEcritureRepository, times(2)).save(ArgumentMatchers.any());
        verify(eventPublisher).publishEvent(ArgumentMatchers.any(VirementEffectueEvent.class));
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
