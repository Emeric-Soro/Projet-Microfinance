package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUtilisateurServiceImplTest {

    @Mock
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Mock
    private PendingActionSubmissionService pendingActionSubmissionService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private RoleUtilisateurServiceImpl roleUtilisateurService;

    @Test
    void shouldSubmitRoleCreation() {
        Utilisateur maker = new Utilisateur();
        maker.setIdUser(1L);
        ActionEnAttente pending = new ActionEnAttente();
        pending.setIdActionEnAttente(4L);

        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(maker);
        when(roleUtilisateurRepository.findByCodeRoleUtilisateur("SECURITY_ADMIN")).thenReturn(Optional.empty());
        when(pendingActionSubmissionService.submit(anyString(), anyString(), anyString(), any(), anyString())).thenReturn(pending);

        ActionEnAttente resultat = roleUtilisateurService.submitCreateRole("security_admin", "Security admin", "Creation role");

        assertThat(resultat.getIdActionEnAttente()).isEqualTo(4L);
        verify(pendingActionSubmissionService).submit(anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void shouldApplyRoleDeletionOnlyWhenUnassigned() {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(6L);
        role.setUtilisateurs(Set.of());
        role.setPermissions(Set.of());

        when(roleUtilisateurRepository.findById(6L)).thenReturn(Optional.of(role));

        roleUtilisateurService.applyDeleteRole(6L);

        verify(roleUtilisateurRepository).delete(role);
    }

    @Test
    void shouldRejectRoleDeletionWhenStillAssignedToUsers() {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(6L);
        role.setUtilisateurs(Set.of(new Utilisateur()));
        role.setPermissions(Set.of());

        when(roleUtilisateurRepository.findById(6L)).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> roleUtilisateurService.applyDeleteRole(6L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Impossible de supprimer un role encore affecte a des utilisateurs");
    }

    @Test
    void shouldApplyRoleUpdateWithNormalizedCode() {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(8L);
        role.setCodeRoleUtilisateur("OLD");

        when(roleUtilisateurRepository.findById(8L)).thenReturn(Optional.of(role));
        when(roleUtilisateurRepository.findByCodeRoleUtilisateur("SUPERVISION_RISQUE")).thenReturn(Optional.empty());
        when(roleUtilisateurRepository.save(any(RoleUtilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleUtilisateur resultat = roleUtilisateurService.applyUpdateRole(8L, "supervision_risque", "Supervision risque");

        assertThat(resultat.getCodeRoleUtilisateur()).isEqualTo("SUPERVISION_RISQUE");
        assertThat(resultat.getIntituleRole()).isEqualTo("Supervision risque");
    }
}
