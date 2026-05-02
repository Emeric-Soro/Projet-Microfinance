package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.PermissionSecuriteRequestDTO;
import com.microfinance.core_banking.dto.request.extension.RolePermissionChangeRequestDTO;
import com.microfinance.core_banking.dto.response.extension.PendingActionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.PermissionSecuriteResponseDTO;
import com.microfinance.core_banking.mapper.extension.PendingActionMapper;
import com.microfinance.core_banking.mapper.extension.PermissionSecuriteMapper;
import com.microfinance.core_banking.service.extension.PermissionSecuriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Gestion backend des permissions fines et habilitations sensibles")
public class PermissionSecuriteController {

    private final PermissionSecuriteService permissionSecuriteService;
    private final PermissionSecuriteMapper permissionSecuriteMapper;
    private final PendingActionMapper pendingActionMapper;

    @GetMapping
    @Operation(summary = "Lister les permissions")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_VIEW)")
    public ResponseEntity<List<PermissionSecuriteResponseDTO>> lister(
            @RequestParam(required = false) String moduleCode,
            @RequestParam(required = false) Boolean actif
    ) {
        return ResponseEntity.ok(permissionSecuriteMapper.toDtoList(permissionSecuriteService.rechercher(moduleCode, actif)));
    }

    @GetMapping("/{idPermission}")
    @Operation(summary = "Consulter une permission")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_VIEW)")
    public ResponseEntity<PermissionSecuriteResponseDTO> detail(@PathVariable Long idPermission) {
        return ResponseEntity.ok(permissionSecuriteMapper.toDto(permissionSecuriteService.getById(idPermission)));
    }

    @GetMapping("/roles/{idRole}")
    @Operation(summary = "Lister les permissions d'un role")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_SUPERVISEUR,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_VIEW)")
    public ResponseEntity<List<PermissionSecuriteResponseDTO>> listerPermissionsRole(@PathVariable Long idRole) {
        return ResponseEntity.ok(permissionSecuriteMapper.toDtoList(permissionSecuriteService.listerPermissionsRole(idRole)));
    }

    @PostMapping
    @Operation(summary = "Soumettre la creation d'une permission")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_MANAGE)")
    @AuditLog(action = "SECURITY_PERMISSION_CREATE_SUBMIT", resource = "PERMISSION_SECURITE")
    public ResponseEntity<PendingActionResponseDTO> creer(@Valid @RequestBody PermissionSecuriteRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(permissionSecuriteService.soumettreCreation(
                requestDTO.getCodePermission(),
                requestDTO.getLibellePermission(),
                requestDTO.getModuleCode(),
                requestDTO.getDescriptionPermission(),
                requestDTO.getActif(),
                requestDTO.getCommentaireMaker()
        )));
    }

    @PutMapping("/{idPermission}")
    @Operation(summary = "Soumettre la mise a jour d'une permission")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_MANAGE)")
    @AuditLog(action = "SECURITY_PERMISSION_UPDATE_SUBMIT", resource = "PERMISSION_SECURITE")
    public ResponseEntity<PendingActionResponseDTO> mettreAJour(
            @PathVariable Long idPermission,
            @Valid @RequestBody PermissionSecuriteRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(permissionSecuriteService.soumettreMiseAJour(
                idPermission,
                requestDTO.getCodePermission(),
                requestDTO.getLibellePermission(),
                requestDTO.getModuleCode(),
                requestDTO.getDescriptionPermission(),
                requestDTO.getActif(),
                requestDTO.getCommentaireMaker()
        )));
    }

    @DeleteMapping("/{idPermission}")
    @Operation(summary = "Soumettre la suppression d'une permission")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_MANAGE)")
    @AuditLog(action = "SECURITY_PERMISSION_DELETE_SUBMIT", resource = "PERMISSION_SECURITE")
    public ResponseEntity<PendingActionResponseDTO> supprimer(
            @PathVariable Long idPermission,
            @Valid @RequestBody(required = false) RolePermissionChangeRequestDTO requestDTO
    ) {
        String commentaire = requestDTO == null ? null : requestDTO.getCommentaireMaker();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(permissionSecuriteService.soumettreSuppression(
                idPermission,
                commentaire
        )));
    }

    @PostMapping("/roles/{idRole}/{idPermission}")
    @Operation(summary = "Soumettre l'affectation d'une permission a un role")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_MANAGE)")
    @AuditLog(action = "SECURITY_ROLE_PERMISSION_ASSIGN_SUBMIT", resource = "ROLE_PERMISSION")
    public ResponseEntity<PendingActionResponseDTO> affecterRole(
            @PathVariable Long idRole,
            @PathVariable Long idPermission,
            @Valid @RequestBody(required = false) RolePermissionChangeRequestDTO requestDTO
    ) {
        String commentaire = requestDTO == null ? null : requestDTO.getCommentaireMaker();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(permissionSecuriteService.soumettreAffectationRole(
                idRole,
                idPermission,
                commentaire
        )));
    }

    @DeleteMapping("/roles/{idRole}/{idPermission}")
    @Operation(summary = "Soumettre la revocation d'une permission d'un role")
    @PreAuthorize("hasAnyAuthority(T(com.microfinance.core_banking.service.security.SecurityConstants).ROLE_ADMIN,T(com.microfinance.core_banking.service.security.SecurityConstants).PERM_SECURITY_PERMISSION_MANAGE)")
    @AuditLog(action = "SECURITY_ROLE_PERMISSION_REVOKE_SUBMIT", resource = "ROLE_PERMISSION")
    public ResponseEntity<PendingActionResponseDTO> revoquerRole(
            @PathVariable Long idRole,
            @PathVariable Long idPermission,
            @Valid @RequestBody(required = false) RolePermissionChangeRequestDTO requestDTO
    ) {
        String commentaire = requestDTO == null ? null : requestDTO.getCommentaireMaker();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(permissionSecuriteService.soumettreRevocationRole(
                idRole,
                idPermission,
                commentaire
        )));
    }
}
