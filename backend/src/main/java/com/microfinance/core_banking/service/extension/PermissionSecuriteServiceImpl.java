package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.entity.PermissionSecurite;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.repository.client.PermissionSecuriteRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PermissionSecuriteServiceImpl implements PermissionSecuriteService {

    private final PermissionSecuriteRepository permissionSecuriteRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PendingActionSubmissionService pendingActionSubmissionService;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionSecurite> rechercher(String moduleCode, Boolean actif) {
        return permissionSecuriteRepository.findAll().stream()
                .filter(permission -> moduleCode == null || moduleCode.isBlank() || permission.getModuleCode().equalsIgnoreCase(moduleCode.trim()))
                .filter(permission -> actif == null || Objects.equals(permission.getActif(), actif))
                .sorted((left, right) -> left.getCodePermission().compareToIgnoreCase(right.getCodePermission()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionSecurite getById(Long idPermission) {
        return permissionSecuriteRepository.findById(idPermission)
                .orElseThrow(() -> new EntityNotFoundException("Permission introuvable: " + idPermission));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionSecurite> listerPermissionsRole(Long idRole) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + idRole));
        return role.getPermissions().stream()
                .sorted((left, right) -> left.getCodePermission().compareToIgnoreCase(right.getCodePermission()))
                .toList();
    }

    @Override
    @Transactional
    public ActionEnAttente soumettreCreation(String codePermission, String libellePermission, String moduleCode, String descriptionPermission, Boolean actif, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        String normalizedCode = normalizeCode(codePermission);
        if (permissionSecuriteRepository.existsByCodePermission(normalizedCode)) {
            throw new IllegalArgumentException("La permission existe deja: " + normalizedCode);
        }
        Map<String, Object> payload = permissionPayload(normalizedCode, libellePermission, moduleCode, descriptionPermission, actif);
        return pendingActionSubmissionService.submit(
                "CREATE_PERMISSION",
                "PERMISSION_SECURITE",
                normalizedCode,
                payload,
                commentaireMaker
        );
    }

    @Override
    @Transactional
    public ActionEnAttente soumettreMiseAJour(Long idPermission, String codePermission, String libellePermission, String moduleCode, String descriptionPermission, Boolean actif, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        PermissionSecurite permission = getById(idPermission);
        Map<String, Object> payload = permissionPayload(codePermission, libellePermission, moduleCode, descriptionPermission, actif);
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "UPDATE_PERMISSION",
                "PERMISSION_SECURITE",
                String.valueOf(idPermission),
                payload,
                commentaireMaker
        );
        action.setAncienneValeur(snapshotPermission(permission).toString());
        return action;
    }

    @Override
    @Transactional
    public ActionEnAttente soumettreSuppression(Long idPermission, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        PermissionSecurite permission = getById(idPermission);
        ActionEnAttente action = pendingActionSubmissionService.submit(
                "DELETE_PERMISSION",
                "PERMISSION_SECURITE",
                String.valueOf(idPermission),
                Map.of("idPermission", idPermission),
                commentaireMaker
        );
        action.setAncienneValeur(snapshotPermission(permission).toString());
        return action;
    }

    @Override
    @Transactional
    public ActionEnAttente soumettreAffectationRole(Long idRole, Long idPermission, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + idRole));
        PermissionSecurite permission = getById(idPermission);
        Map<String, Object> payload = Map.of(
                "idRole", idRole,
                "idPermission", idPermission
        );
        return pendingActionSubmissionService.submit(
                "ASSIGN_PERMISSION_ROLE",
                "ROLE_PERMISSION",
                idRole + ":" + idPermission,
                payload,
                defaultedComment(commentaireMaker, "Affectation permission " + permission.getCodePermission() + " au role " + role.getCodeRoleUtilisateur())
        );
    }

    @Override
    @Transactional
    public ActionEnAttente soumettreRevocationRole(Long idRole, Long idPermission, String commentaireMaker) {
        authenticatedUserService.getCurrentUserOrThrow();
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + idRole));
        PermissionSecurite permission = getById(idPermission);
        Map<String, Object> payload = Map.of(
                "idRole", idRole,
                "idPermission", idPermission
        );
        return pendingActionSubmissionService.submit(
                "REVOKE_PERMISSION_ROLE",
                "ROLE_PERMISSION",
                idRole + ":" + idPermission,
                payload,
                defaultedComment(commentaireMaker, "Revocation permission " + permission.getCodePermission() + " du role " + role.getCodeRoleUtilisateur())
        );
    }

    @Override
    @Transactional
    public PermissionSecurite appliquerCreation(Map<String, Object> payload) {
        String normalizedCode = normalizeCode(required(payload, "codePermission"));
        if (permissionSecuriteRepository.existsByCodePermission(normalizedCode)) {
            throw new IllegalArgumentException("La permission existe deja: " + normalizedCode);
        }
        PermissionSecurite permission = new PermissionSecurite();
        hydratePermission(permission, payload);
        permission.setCodePermission(normalizedCode);
        return permissionSecuriteRepository.save(permission);
    }

    @Override
    @Transactional
    public PermissionSecurite appliquerMiseAJour(Long idPermission, Map<String, Object> payload) {
        PermissionSecurite permission = getById(idPermission);
        String normalizedCode = normalizeCode(required(payload, "codePermission"));
        permissionSecuriteRepository.findByCodePermission(normalizedCode)
                .filter(existing -> !existing.getIdPermission().equals(idPermission))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Le code permission est deja utilise: " + normalizedCode);
                });
        hydratePermission(permission, payload);
        permission.setCodePermission(normalizedCode);
        return permissionSecuriteRepository.save(permission);
    }

    @Override
    @Transactional
    public void appliquerSuppression(Long idPermission) {
        PermissionSecurite permission = getById(idPermission);
        if (!permission.getRoles().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer une permission encore affectee a des roles");
        }
        permissionSecuriteRepository.delete(permission);
    }

    @Override
    @Transactional
    public RoleUtilisateur appliquerAffectationRole(Long idRole, Long idPermission) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + idRole));
        PermissionSecurite permission = getById(idPermission);
        role.getPermissions().add(permission);
        return roleUtilisateurRepository.save(role);
    }

    @Override
    @Transactional
    public RoleUtilisateur appliquerRevocationRole(Long idRole, Long idPermission) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(idRole)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + idRole));
        PermissionSecurite permission = getById(idPermission);
        role.getPermissions().remove(permission);
        return roleUtilisateurRepository.save(role);
    }

    private Map<String, Object> permissionPayload(String codePermission, String libellePermission, String moduleCode, String descriptionPermission, Boolean actif) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("codePermission", normalizeCode(codePermission));
        payload.put("libellePermission", requiredString(libellePermission, "libellePermission"));
        payload.put("moduleCode", normalizeCode(moduleCode));
        payload.put("descriptionPermission", descriptionPermission == null ? null : descriptionPermission.trim());
        payload.put("actif", actif == null ? Boolean.TRUE : actif);
        return payload;
    }

    private Map<String, Object> snapshotPermission(PermissionSecurite permission) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("idPermission", permission.getIdPermission());
        snapshot.put("codePermission", permission.getCodePermission());
        snapshot.put("libellePermission", permission.getLibellePermission());
        snapshot.put("moduleCode", permission.getModuleCode());
        snapshot.put("descriptionPermission", permission.getDescriptionPermission());
        snapshot.put("actif", permission.getActif());
        return snapshot;
    }

    private void hydratePermission(PermissionSecurite permission, Map<String, Object> payload) {
        permission.setLibellePermission(required(payload, "libellePermission"));
        permission.setModuleCode(normalizeCode(required(payload, "moduleCode")));
        permission.setDescriptionPermission(optional(payload, "descriptionPermission"));
        permission.setActif(payload.get("actif") == null || Boolean.parseBoolean(payload.get("actif").toString()));
    }

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.toString().trim();
    }

    private String requiredString(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le champ '" + key + "' est obligatoire");
        }
        return value.trim();
    }

    private String optional(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value == null || value.toString().isBlank() ? null : value.toString().trim();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code est obligatoire");
        }
        return code.trim().toUpperCase();
    }

    private String defaultedComment(String commentaire, String fallback) {
        return commentaire == null || commentaire.isBlank() ? fallback : commentaire.trim();
    }
}
