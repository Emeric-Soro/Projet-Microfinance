package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.extension.ActionEnAttenteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationExtensionServiceTest {

    @Mock
    private ActionEnAttenteRepository actionEnAttenteRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private PendingActionExecutionService pendingActionExecutionService;
    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ValidationExtensionService validationExtensionService;

    @Test
    void validerAction_utiliseLeCheckerAuthentifie() {
        Utilisateur maker = utilisateur(1L, "GUICHETIER");
        Utilisateur checker = utilisateur(2L, "SUPERVISEUR");
        ActionEnAttente action = new ActionEnAttente();
        action.setIdActionEnAttente(10L);
        action.setMaker(maker);
        action.setStatut("EN_ATTENTE");

        when(actionEnAttenteRepository.findById(10L)).thenReturn(Optional.of(action));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(authenticatedUserService.hasAnyRoleOrPermission(any(String[].class), any(String[].class))).thenReturn(true);
        when(actionEnAttenteRepository.save(any(ActionEnAttente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionEnAttente resultat = validationExtensionService.validerAction(10L, Map.of(
                "statut", "REJETEE",
                "commentaireChecker", "Motif documente",
                "idChecker", 999L
        ));

        assertThat(resultat.getChecker()).isEqualTo(checker);
        assertThat(resultat.getStatut()).isEqualTo("REJETEE");
        verify(utilisateurRepository, never()).findById(999L);
    }

    @Test
    void validerAction_refuseUnMakerCommeChecker() {
        Utilisateur maker = utilisateur(1L, "SUPERVISEUR");
        ActionEnAttente action = new ActionEnAttente();
        action.setIdActionEnAttente(10L);
        action.setMaker(maker);

        when(actionEnAttenteRepository.findById(10L)).thenReturn(Optional.of(action));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(maker);
        when(authenticatedUserService.hasAnyRoleOrPermission(any(String[].class), any(String[].class))).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> validationExtensionService.validerAction(10L, Map.of("statut", "APPROUVEE")));
        verify(pendingActionExecutionService, never()).execute(any());
    }

    @Test
    void validerAction_refuseUneActionDejaDecidee() {
        Utilisateur maker = utilisateur(1L, "GUICHETIER");
        Utilisateur checker = utilisateur(2L, "SUPERVISEUR");
        ActionEnAttente action = new ActionEnAttente();
        action.setIdActionEnAttente(10L);
        action.setMaker(maker);
        action.setStatut("APPROUVEE");

        when(actionEnAttenteRepository.findById(10L)).thenReturn(Optional.of(action));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(checker);

        assertThrows(IllegalStateException.class, () -> validationExtensionService.validerAction(10L, Map.of("statut", "REJETEE")));
        verify(pendingActionExecutionService, never()).execute(any());
    }

    @Test
    void validerAction_exigeUnCommentairePourUnRejet() {
        Utilisateur maker = utilisateur(1L, "GUICHETIER");
        Utilisateur checker = utilisateur(2L, "SUPERVISEUR");
        ActionEnAttente action = new ActionEnAttente();
        action.setIdActionEnAttente(10L);
        action.setMaker(maker);
        action.setStatut("EN_ATTENTE");

        when(actionEnAttenteRepository.findById(10L)).thenReturn(Optional.of(action));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(authenticatedUserService.hasAnyRoleOrPermission(any(String[].class), any(String[].class))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> validationExtensionService.validerAction(10L, Map.of("statut", "REJETEE")));
        verify(pendingActionExecutionService, never()).execute(any());
    }

    @Test
    void validerAction_accepteUnCheckerPorteParPermission() {
        Utilisateur maker = utilisateur(1L, "GUICHETIER");
        Utilisateur checker = utilisateur(2L, "ANALYSTE");
        ActionEnAttente action = new ActionEnAttente();
        action.setIdActionEnAttente(10L);
        action.setMaker(maker);
        action.setStatut("EN_ATTENTE");

        when(actionEnAttenteRepository.findById(10L)).thenReturn(Optional.of(action));
        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(checker);
        when(authenticatedUserService.hasAnyRoleOrPermission(any(String[].class), any(String[].class))).thenReturn(true);
        when(actionEnAttenteRepository.save(any(ActionEnAttente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActionEnAttente resultat = validationExtensionService.validerAction(10L, Map.of(
                "statut", "CORRECTION_DEMANDEE",
                "commentaireChecker", "Piece justificative incomplete"
        ));

        assertThat(resultat.getChecker()).isEqualTo(checker);
        assertThat(resultat.getStatut()).isEqualTo("CORRECTION_DEMANDEE");
        verify(pendingActionExecutionService, never()).execute(any());
    }

    private Utilisateur utilisateur(Long idUser, String roleCode) {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur(roleCode);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(idUser);
        utilisateur.setRoles(Set.of(role));
        return utilisateur;
    }
}
