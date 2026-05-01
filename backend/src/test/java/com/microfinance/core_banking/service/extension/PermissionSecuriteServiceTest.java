package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.PermissionSecuriteRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionSecuriteServiceTest {

    @Mock
    private PermissionSecuriteRepository permissionSecuriteRepository;

    @Mock
    private RoleUtilisateurRepository roleUtilisateurRepository;

    @Mock
    private PendingActionSubmissionService pendingActionSubmissionService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private PermissionSecuriteServiceImpl permissionSecuriteService;

    @Test
    void shouldSubmitPermissionCreationForMakerChecker() {
        Utilisateur maker = new Utilisateur();
        maker.setIdUser(7L);
        ActionEnAttente pending = new ActionEnAttente();
        pending.setIdActionEnAttente(12L);

        when(authenticatedUserService.getCurrentUserOrThrow()).thenReturn(maker);
        when(permissionSecuriteRepository.existsByCodePermission("SECURITY_PERMISSION_VIEW")).thenReturn(false);
        when(pendingActionSubmissionService.submit(anyString(), anyString(), anyString(), any(), anyString())).thenReturn(pending);

        ActionEnAttente resultat = permissionSecuriteService.soumettreCreation(
                "security_permission_view",
                "Consulter les permissions",
                "security",
                "Lecture",
                true,
                "Creation permission"
        );

        assertThat(resultat.getIdActionEnAttente()).isEqualTo(12L);
        verify(pendingActionSubmissionService).submit(
                anyString(),
                anyString(),
                anyString(),
                any(),
                anyString()
        );
    }

    @Test
    void shouldApplyRolePermissionAssignment() {
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(5L);
        role.setCodeRoleUtilisateur("ADMIN");

        PermissionSecurite permission = new PermissionSecurite();
        permission.setIdPermission(9L);
        permission.setCodePermission("SECURITY_PERMISSION_VIEW");
        permission.setActif(true);

        when(roleUtilisateurRepository.findById(5L)).thenReturn(Optional.of(role));
        when(permissionSecuriteRepository.findById(9L)).thenReturn(Optional.of(permission));
        when(roleUtilisateurRepository.save(any(RoleUtilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoleUtilisateur resultat = permissionSecuriteService.appliquerAffectationRole(5L, 9L);

        assertThat(resultat.getPermissions()).contains(permission);
    }

    @Test
    void shouldRejectPermissionDeletionWhenStillAssignedToRole() {
        PermissionSecurite permission = new PermissionSecurite();
        permission.setIdPermission(3L);
        permission.setCodePermission("SECURITY_PERMISSION_MANAGE");
        permission.setRoles(Set.of(new RoleUtilisateur()));

        when(permissionSecuriteRepository.findById(3L)).thenReturn(Optional.of(permission));

        assertThatThrownBy(() -> permissionSecuriteService.appliquerSuppression(3L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Impossible de supprimer une permission encore affectee a des roles");
    }

    @Test
    void shouldApplyPermissionUpdateWithNormalizedCodes() {
        PermissionSecurite permission = new PermissionSecurite();
        permission.setIdPermission(4L);
        permission.setCodePermission("OLD_CODE");

        when(permissionSecuriteRepository.findById(4L)).thenReturn(Optional.of(permission));
        when(permissionSecuriteRepository.findByCodePermission("SECURITY_AUDIT_VIEW")).thenReturn(Optional.empty());
        when(permissionSecuriteRepository.save(any(PermissionSecurite.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PermissionSecurite resultat = permissionSecuriteService.appliquerMiseAJour(4L, Map.of(
                "codePermission", "security_audit_view",
                "libellePermission", "Consulter audit",
                "moduleCode", "security",
                "descriptionPermission", "Lecture audit",
                "actif", true
        ));

        assertThat(resultat.getCodePermission()).isEqualTo("SECURITY_AUDIT_VIEW");
        assertThat(resultat.getModuleCode()).isEqualTo("SECURITY");
    }
}
