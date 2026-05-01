package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleUtilisateurServiceImpl implements RoleUtilisateurService {

    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PendingActionSubmissionService pendingActionSubmissionService;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    @Transactional(readOnly = true)
    public List<RoleUtilisateur> getAllRoles() {
        return roleUtilisateurRepository.findAll();
    }

    @Override
    @Transactional
    public ActionEnAttente submitCreateRole(String code, String intitule, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        String normalizedCode = normalizeCode(code);
        if (roleUtilisateurRepository.findByCodeRoleUtilisateur(normalizedCode).isPresent()) {
            throw new IllegalArgumentException("Le role avec ce code existe deja: " + normalizedCode);
        }
        return pendingActionSubmissionService.submit(
                "CREATE_ROLE",
                "ROLE_UTILISATEUR",
                normalizedCode,
                rolePayload(normalizedCode, intitule),
                commentaireMaker
        );
    }

    @Override
    @Transactional
    public ActionEnAttente submitUpdateRole(Long idRole, String code, String intitule, String commentaireMaker) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable avec id: " + idRole));
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_ROLE",
                "ROLE_UTILISATEUR",
                String.valueOf(idRole),
                rolePayload(code, intitule),
                commentaireMaker
        );
        action.setAncienneValeur(roleSnapshot(role).toString());
        return action;
    }

    @Override
    @Transactional
    public ActionEnAttente submitDeleteRole(Long idRole, String commentaireMaker) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable avec id: " + idRole));
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "DELETE_ROLE",
                "ROLE_UTILISATEUR",
                String.valueOf(idRole),
                Map.of("idRole", idRole),
                commentaireMaker
        );
        action.setAncienneValeur(roleSnapshot(role).toString());
        return action;
    }

    @Override
    @Transactional
    public RoleUtilisateur applyCreateRole(String code, String intitule) {
        String normalizedCode = normalizeCode(code);
        if (roleUtilisateurRepository.findByCodeRoleUtilisateur(normalizedCode).isPresent()) {
            throw new IllegalArgumentException("Le role avec ce code existe deja: " + normalizedCode);
        }
        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur(normalizedCode);
        role.setIntituleRole(required(intitule, "intituleRole"));
        return roleUtilisateurRepository.save(role);
    }

    @Override
    @Transactional
    public RoleUtilisateur applyUpdateRole(Long idRole, String code, String intitule) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable avec id: " + idRole));
        String normalizedCode = normalizeCode(code);
        roleUtilisateurRepository.findByCodeRoleUtilisateur(normalizedCode)
                .filter(existing -> !existing.getIdRole().equals(idRole))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Le role avec ce code existe deja: " + normalizedCode);
                });
        role.setCodeRoleUtilisateur(normalizedCode);
        role.setIntituleRole(required(intitule, "intituleRole"));
        return roleUtilisateurRepository.save(role);
    }

    @Override
    @Transactional
    public void applyDeleteRole(Long idRole) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable avec id: " + idRole));
        if (!role.getUtilisateurs().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un role encore affecte a des utilisateurs");
        }
        if (!role.getPermissions().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un role encore affecte a des permissions");
        }
        roleUtilisateurRepository.delete(role);
    }

    private Map<String, Object> rolePayload(String code, String intitule) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("codeRoleUtilisateur", normalizeCode(code));
        payload.put("intituleRole", required(intitule, "intituleRole"));
        return payload;
    }

    private Map<String, Object> roleSnapshot(RoleUtilisateur role) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("idRole", role.getIdRole());
        snapshot.put("codeRoleUtilisateur", role.getCodeRoleUtilisateur());
        snapshot.put("intituleRole", role.getIntituleRole());
        snapshot.put("permissions", role.getPermissions().stream().map(permission -> permission.getCodePermission()).sorted().toList());
        return snapshot;
    }

    private String normalizeCode(String code) {
        return required(code, "codeRoleUtilisateur").toUpperCase();
    }

    private String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + fieldName + "' est obligatoire");
        }
        return value.trim();
    }
}
