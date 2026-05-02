package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.CompteComptable;
import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.Employe;
import com.microfinance.core_banking.entity.MutationPersonnel;
import com.microfinance.core_banking.entity.OperationDeplacee;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeTransaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.AffectationUtilisateurAgenceRepository;
import com.microfinance.core_banking.repository.extension.AgenceRepository;
import com.microfinance.core_banking.repository.extension.CommissionInterAgenceRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteLiaisonAgenceRepository;
import com.microfinance.core_banking.repository.extension.CreditRepository;
import com.microfinance.core_banking.repository.extension.EmployeRepository;
import com.microfinance.core_banking.repository.extension.GuichetRepository;
import com.microfinance.core_banking.repository.extension.MutationPersonnelRepository;
import com.microfinance.core_banking.repository.extension.OperationDeplaceeRepository;
import com.microfinance.core_banking.repository.extension.ParametreAgenceRepository;
import com.microfinance.core_banking.repository.extension.RegionRepository;
import com.microfinance.core_banking.repository.extension.RapprochementInterAgenceRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microfinance.core_banking.dto.request.extension.OperationDeplaceeRequestDTO;
import com.microfinance.core_banking.dto.request.extension.ValiderMutationRequestDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationServiceTest {

    @Mock
    private RegionRepository regionRepository;
    @Mock
    private AgenceRepository agenceRepository;
    @Mock
    private GuichetRepository guichetRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private AffectationUtilisateurAgenceRepository affectationRepository;
    @Mock
    private ParametreAgenceRepository parametreAgenceRepository;
    @Mock
    private EmployeRepository employeRepository;
    @Mock
    private MutationPersonnelRepository mutationPersonnelRepository;
    @Mock
    private CompteComptableRepository compteComptableRepository;
    @Mock
    private CompteLiaisonAgenceRepository compteLiaisonAgenceRepository;
    @Mock
    private OperationDeplaceeRepository operationDeplaceeRepository;
    @Mock
    private CommissionInterAgenceRepository commissionInterAgenceRepository;
    @Mock
    private RapprochementInterAgenceRepository rapprochementInterAgenceRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CompteRepository compteRepository;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private OrganisationService organisationService;

    @Test
    void validerMutationPersonnel_approuvee_metAJourEmployeEtMutation() {
        Agence agenceSource = agence(10L, "AG-10");
        Agence agenceDestination = agence(20L, "AG-20");
        Employe employe = new Employe();
        employe.setIdEmploye(7L);
        employe.setAgence(agenceSource);

        MutationPersonnel mutation = new MutationPersonnel();
        mutation.setIdMutationPersonnel(55L);
        mutation.setEmploye(employe);
        mutation.setAgenceSource(agenceSource);
        mutation.setAgenceDestination(agenceDestination);
        mutation.setDateMutation(LocalDate.now());
        mutation.setStatut("INITIEE");

        Utilisateur validateur = new Utilisateur();
        validateur.setIdUser(99L);

        when(mutationPersonnelRepository.findById(55L)).thenReturn(Optional.of(mutation));
        when(utilisateurRepository.findById(99L)).thenReturn(Optional.of(validateur));
        when(employeRepository.save(any(Employe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mutationPersonnelRepository.save(any(MutationPersonnel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ValiderMutationRequestDTO payload = new ValiderMutationRequestDTO();
        payload.setDecision("APPROUVEE");
        payload.setIdValidateur(99L);
        payload.setCommentaireValidation("Mutation approuvee");

        MutationPersonnel resultat = organisationService.validerMutationPersonnel(55L, payload);

        assertEquals("VALIDEE", resultat.getStatut());
        assertEquals(agenceDestination, employe.getAgence());
        assertEquals(validateur, resultat.getValidateur());
        assertNotNull(resultat.getDateValidation());
        verify(authenticatedUserService).assertAgencyAccess(10L);
        verify(authenticatedUserService).assertAgencyAccess(20L);
        verify(employeRepository).save(employe);
    }

    @Test
    void validerMutationPersonnel_decisionInvalide_leveUneErreur() {
        Agence agenceSource = agence(10L, "AG-10");
        Agence agenceDestination = agence(20L, "AG-20");
        Employe employe = new Employe();
        employe.setIdEmploye(7L);
        employe.setAgence(agenceSource);

        MutationPersonnel mutation = new MutationPersonnel();
        mutation.setIdMutationPersonnel(66L);
        mutation.setEmploye(employe);
        mutation.setAgenceSource(agenceSource);
        mutation.setAgenceDestination(agenceDestination);

        when(mutationPersonnelRepository.findById(66L)).thenReturn(Optional.of(mutation));

        IllegalArgumentException erreur = assertThrows(
                IllegalArgumentException.class,
                () -> organisationService.validerMutationPersonnel(66L, new ValiderMutationRequestDTO("A_REVOIR", null, null))
        );

        assertEquals("Decision de mutation non supportee: A_REVOIR", erreur.getMessage());
        verify(employeRepository, never()).save(any(Employe.class));
    }

    @Test
    void enregistrerOperationDeplacee_avecCommission_creeOperationEtCommission() {
        Agence agenceOrigine = agence(1L, "AG-001");
        Agence agenceOperante = agence(2L, "AG-002");

        TypeTransaction typeTransaction = new TypeTransaction();
        typeTransaction.setCodeTypeTransaction("DEPOT");

        Transaction transaction = new Transaction();
        transaction.setIdTransaction(91L);
        transaction.setReferenceUnique("TX-91");
        transaction.setTypeTransaction(typeTransaction);
        transaction.setMontantGlobal(new BigDecimal("1000.00"));

        CompteComptable compteComptable = new CompteComptable();
        compteComptable.setIdCompteComptable(7L);

        when(transactionRepository.findById(91L)).thenReturn(Optional.of(transaction));
        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agenceOrigine));
        when(agenceRepository.findById(2L)).thenReturn(Optional.of(agenceOperante));
        when(compteComptableRepository.findById(7L)).thenReturn(Optional.of(compteComptable));
        when(operationDeplaceeRepository.save(any(OperationDeplacee.class))).thenAnswer(invocation -> {
            OperationDeplacee operation = invocation.getArgument(0);
            operation.setIdOperationDeplacee(500L);
            return operation;
        });
        when(commissionInterAgenceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OperationDeplaceeRequestDTO payload = new OperationDeplaceeRequestDTO();
        payload.setIdTransaction(91L);
        payload.setIdAgenceOrigine(1L);
        payload.setIdAgenceOperante(2L);
        payload.setTauxCommission(new BigDecimal("2.5"));
        payload.setIdCompteComptable(7L);

        OperationDeplacee resultat = organisationService.enregistrerOperationDeplacee(payload);

        assertEquals(500L, resultat.getIdOperationDeplacee());
        assertEquals("DEPOT", resultat.getTypeOperation());
        assertEquals(0, new BigDecimal("1000.00").compareTo(resultat.getMontant()));
        verify(authenticatedUserService).assertAgencyAccess(1L);
        verify(authenticatedUserService).assertAgencyAccess(2L);
        ArgumentCaptor<com.microfinance.core_banking.entity.CommissionInterAgence> captor = ArgumentCaptor.forClass(com.microfinance.core_banking.entity.CommissionInterAgence.class);
        verify(commissionInterAgenceRepository).save(captor.capture());
        assertEquals(0, new BigDecimal("25.00").compareTo(captor.getValue().getMontantCommission()));
    }

    private Agence agence(Long idAgence, String codeAgence) {
        Agence agence = new Agence();
        agence.setIdAgence(idAgence);
        agence.setCodeAgence(codeAgence);
        agence.setNomAgence(codeAgence);
        return agence;
    }
}
