package com.microfinance.core_banking.service.security;

import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.microfinance.core_banking.service.security.SecurityConstants.*;

@Service
public class AuthenticatedUserService {

    private static final Set<String> GLOBAL_SCOPE_ROLES = Set.of(ROLE_ADMIN, ROLE_SUPERVISEUR);

    public Optional<Utilisateur> getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof Utilisateur utilisateur) {
            return Optional.of(utilisateur);
        }
        return Optional.empty();
    }

    public Utilisateur getCurrentUserOrThrow() {
        return getCurrentUserOptional()
                .orElseThrow(() -> new IllegalStateException("Utilisateur authentifie introuvable"));
    }

    public boolean hasGlobalScope() {
        return getCurrentUserOptional()
                .map(user -> user.getRoles().stream()
                        .map(RoleUtilisateur::getCodeRoleUtilisateur)
                        .map(String::toUpperCase)
                        .anyMatch(GLOBAL_SCOPE_ROLES::contains))
                .orElse(true);
    }

    public boolean hasAnyRole(String... roles) {
        Set<String> expectedRoles = Set.of(roles).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return getCurrentUserOptional()
                .map(user -> user.getRoles().stream()
                        .map(RoleUtilisateur::getCodeRoleUtilisateur)
                        .map(String::toUpperCase)
                        .anyMatch(expectedRoles::contains))
                .orElse(false);
    }

    public boolean hasAnyPermission(String... permissions) {
        Set<String> expectedPermissions = Set.of(permissions).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return getCurrentUserOptional()
                .map(user -> user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .filter(permission -> Boolean.TRUE.equals(permission.getActif()))
                        .map(permission -> permission.getCodePermission().toUpperCase())
                .anyMatch(expectedPermissions::contains))
                .orElse(false);
    }

    public boolean hasAnyRoleOrPermission(String[] roles, String[] permissions) {
        return hasAnyRole(roles) || hasAnyPermission(permissions);
    }

    public Long getCurrentAgencyId() {
        return getCurrentUserOptional()
                .map(Utilisateur::getAgenceActive)
                .map(agence -> agence.getIdAgence())
                .orElse(null);
    }

    public void assertAgencyAccess(Long idAgence) {
        if (idAgence == null || hasGlobalScope()) {
            return;
        }
        Long currentAgencyId = getCurrentAgencyId();
        if (currentAgencyId == null || !currentAgencyId.equals(idAgence)) {
            throw new IllegalStateException("Acces refuse hors perimetre agence");
        }
    }
}
