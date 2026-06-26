package com.soutra.microfinance.api.controller.securite;

import com.soutra.microfinance.audit.AuditLog;
import com.soutra.microfinance.dto.request.client.*;
import com.soutra.microfinance.dto.response.client.*;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.mapper.UtilisateurMapper;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.service.client.SessionService;
import com.soutra.microfinance.service.client.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/securite")
@Tag(name = "Securite Admin", description = "API d'administration de la securite (roles, permissions, utilisateurs, sessions)")
@PreAuthorize("hasAuthority('ADMIN')")
public class SecurityAdminController {

    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final SessionService sessionService;

    public SecurityAdminController(
            RoleUtilisateurRepository roleUtilisateurRepository,
            UtilisateurRepository utilisateurRepository,
            UtilisateurService utilisateurService,
            UtilisateurMapper utilisateurMapper,
            SessionService sessionService
    ) {
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.sessionService = sessionService;
    }

    @Operation(summary = "Lister tous les roles", description = "Retourne la liste complete des roles du systeme")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Liste des roles") })
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponseDTO>> listerRoles() {
        List<RoleUtilisateur> roles = roleUtilisateurRepository.findAll();
        List<RoleResponseDTO> dtos = roles.stream()
                .map(r -> new RoleResponseDTO(
                        r.getIdRole(),
                        r.getCodeRoleUtilisateur(),
                        r.getIntituleRole(),
                        r.getUtilisateurs() != null ? r.getUtilisateurs().size() : 0,
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Creer un role", description = "Cree un nouveau role dans le systeme")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role cree"),
            @ApiResponse(responseCode = "409", description = "Code role deja existant")
    })
    @PostMapping("/roles")
    @AuditLog(action = "SECURITY_CREATE_ROLE", resource = "SECURITE")
    public ResponseEntity<RoleResponseDTO> creerRole(@Valid @RequestBody CreerRoleRequestDTO requestDTO) {
        if (roleUtilisateurRepository.findByCodeRoleUtilisateur(requestDTO.getCodeRole()).isPresent()) {
            throw new IllegalStateException("Role deja existant: " + requestDTO.getCodeRole());
        }
        RoleUtilisateur role = new RoleUtilisateur();
        role.setCodeRoleUtilisateur(requestDTO.getCodeRole());
        role.setIntituleRole(requestDTO.getIntitule());
        RoleUtilisateur sauvegarde = roleUtilisateurRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoleResponseDTO(
                sauvegarde.getIdRole(), sauvegarde.getCodeRoleUtilisateur(),
                sauvegarde.getIntituleRole(), 0,
                sauvegarde.getCreatedAt(), sauvegarde.getUpdatedAt()));
    }

    @Operation(summary = "Modifier un role", description = "Modifie le code et/ou l'intitule d'un role existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role modifie"),
            @ApiResponse(responseCode = "404", description = "Role introuvable")
    })
    @PutMapping("/roles/{id}")
    @AuditLog(action = "SECURITY_UPDATE_ROLE", resource = "SECURITE")
    public ResponseEntity<RoleResponseDTO> modifierRole(
            @PathVariable Long id,
            @Valid @RequestBody ModifierRoleRequestDTO requestDTO
    ) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + id));
        role.setCodeRoleUtilisateur(requestDTO.getCodeRole());
        role.setIntituleRole(requestDTO.getIntitule());
        RoleUtilisateur sauvegarde = roleUtilisateurRepository.save(role);
        return ResponseEntity.ok(new RoleResponseDTO(
                sauvegarde.getIdRole(), sauvegarde.getCodeRoleUtilisateur(),
                sauvegarde.getIntituleRole(),
                sauvegarde.getUtilisateurs() != null ? sauvegarde.getUtilisateurs().size() : 0,
                sauvegarde.getCreatedAt(), sauvegarde.getUpdatedAt()));
    }

    @Operation(summary = "Supprimer un role", description = "Supprime un role du systeme")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role supprime"),
            @ApiResponse(responseCode = "409", description = "Role non supprimable (utilisateurs attaches)")
    })
    @DeleteMapping("/roles/{id}")
    @AuditLog(action = "SECURITY_DELETE_ROLE", resource = "SECURITE")
    public ResponseEntity<Void> supprimerRole(@PathVariable Long id) {
        RoleUtilisateur role = roleUtilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + id));
        if (role.getUtilisateurs() != null && !role.getUtilisateurs().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un role avec des utilisateurs attaches");
        }
        roleUtilisateurRepository.delete(role);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lister les permissions", description = "Retourne les permissions associees a chaque role")
    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseDTO>> listerPermissions(
            @RequestParam(required = false) String codeRole
    ) {
        List<RoleUtilisateur> roles;
        if (codeRole != null && !codeRole.isBlank()) {
            RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                    .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + codeRole));
            roles = List.of(role);
        } else {
            roles = roleUtilisateurRepository.findAll();
        }

        List<PermissionResponseDTO> dtos = roles.stream()
                .map(r -> new PermissionResponseDTO(
                        r.getCodeRoleUtilisateur(),
                        Set.of(r.getCodeRoleUtilisateur())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Mettre a jour les permissions", description = "Met a jour les permissions d'un role (actuellement: role-based)")
    @PutMapping("/permissions")
    @AuditLog(action = "SECURITY_UPDATE_PERMISSIONS", resource = "SECURITE")
    public ResponseEntity<PermissionResponseDTO> mettreAJourPermissions(
            @Valid @RequestBody ModifierPermissionsRequestDTO requestDTO
    ) {
        RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(requestDTO.getCodeRole())
                .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + requestDTO.getCodeRole()));

        return ResponseEntity.ok(new PermissionResponseDTO(
                role.getCodeRoleUtilisateur(),
                Set.of(role.getCodeRoleUtilisateur())
        ));
    }

    @Operation(summary = "Lister les utilisateurs", description = "Liste paginee des utilisateurs avec filtres optionnels")
    @GetMapping("/utilisateurs")
    public ResponseEntity<Page<UtilisateurResponseDTO>> listerUtilisateurs(
            @RequestParam(required = false) String codeRole,
            @RequestParam(required = false) Boolean actif,
            @ParameterObject Pageable pageable
    ) {
        Page<Utilisateur> utilisateurs;
        if (codeRole != null && !codeRole.isBlank()) {
            RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                    .orElseThrow(() -> new EntityNotFoundException("Role introuvable: " + codeRole));
            utilisateurs = utilisateurRepository.findByRolesContaining(role, pageable);
        } else if (actif != null) {
            utilisateurs = utilisateurRepository.findByActif(actif, pageable);
        } else {
            utilisateurs = utilisateurRepository.findAll(pageable);
        }
        return ResponseEntity.ok(utilisateurs.map(utilisateurMapper::toResponseDTO));
    }

    @Operation(summary = "Creer un utilisateur (admin)", description = "Cree un acces web pour un client")
    @PostMapping("/utilisateurs")
    @AuditLog(action = "SECURITY_CREATE_USER", resource = "SECURITE")
    public ResponseEntity<UtilisateurResponseDTO> creerUtilisateur(
            @Valid @RequestBody CreationUtilisateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.creerCompteWeb(
                requestDTO.getCodeClient(),
                requestDTO.getEmail(),
                requestDTO.getDateNaissance(),
                requestDTO.getMotDePasseBrut()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(summary = "Creer un collaborateur (membre du personnel)", description = "Cree un profil client et un acces web pour un membre du personnel")
    @PostMapping("/utilisateurs/collaborateur")
    @AuditLog(action = "SECURITY_CREATE_COLLABORATEUR", resource = "SECURITE")
    public ResponseEntity<UtilisateurResponseDTO> creerCollaborateur(
            @Valid @RequestBody CreationCollaborateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.creerCollaborateur(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(summary = "Modifier un utilisateur", description = "Modifie les informations d'un utilisateur existant")
    @PutMapping("/utilisateurs/{id}")
    @AuditLog(action = "SECURITY_UPDATE_USER", resource = "SECURITE")
    public ResponseEntity<UtilisateurResponseDTO> modifierUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody CreationUtilisateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.modifierCompteWeb(
                id,
                requestDTO.getEmail(),
                requestDTO.getMotDePasseBrut()
        );
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(summary = "Activer/desactiver un utilisateur", description = "Active ou desactive l'acces d'un utilisateur")
    @PutMapping("/utilisateurs/{id}/activer-desactiver")
    @AuditLog(action = "SECURITY_TOGGLE_USER", resource = "SECURITE")
    public ResponseEntity<UtilisateurResponseDTO> activerDesactiver(
            @PathVariable Long id,
            @Valid @RequestBody ActivationUtilisateurRequestDTO requestDTO
    ) {
        Utilisateur utilisateur = utilisateurService.changerActivation(id, requestDTO.getActif());
        return ResponseEntity.ok(utilisateurMapper.toResponseDTO(utilisateur));
    }

    @Operation(summary = "Lister toutes les sessions", description = "Liste toutes les sessions actives du systeme")
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionActiveResponseDTO>> listerSessions() {
        return ResponseEntity.ok(sessionService.listerToutesSessions());
    }

    @Operation(summary = "Revoquer une session (admin)", description = "Revoque n'importe quelle session active")
    @PostMapping("/sessions/revoguer")
    @AuditLog(action = "SECURITY_REVOKE_SESSION", resource = "SECURITE")
    public ResponseEntity<Void> revoguerSession(@Valid @RequestBody RevoguerSessionRequestDTO requestDTO) {
        sessionService.revoquerSession(null, requestDTO.getSessionId());
        return ResponseEntity.noContent().build();
    }
}
