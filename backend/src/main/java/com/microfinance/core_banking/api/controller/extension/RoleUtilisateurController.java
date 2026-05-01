package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.audit.AuditLog;
import com.microfinance.core_banking.dto.request.extension.RoleUtilisateurRequestDTO;
import com.microfinance.core_banking.dto.response.extension.PendingActionResponseDTO;
import com.microfinance.core_banking.dto.response.extension.RoleUtilisateurResponseDTO;
import com.microfinance.core_banking.mapper.extension.PendingActionMapper;
import com.microfinance.core_banking.mapper.extension.RoleUtilisateurMapper;
import com.microfinance.core_banking.service.extension.RoleUtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Role and Access Control management API")
public class RoleUtilisateurController {

    private final RoleUtilisateurService roleUtilisateurService;
    private final RoleUtilisateurMapper roleUtilisateurMapper;
    private final PendingActionMapper pendingActionMapper;

    @GetMapping
    @Operation(summary = "Get all roles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'SECURITY_PERMISSION_VIEW')")
    public ResponseEntity<List<RoleUtilisateurResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleUtilisateurMapper.toDtoList(roleUtilisateurService.getAllRoles()));
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECURITY_PERMISSION_MANAGE')")
    @AuditLog(action = "ROLE_CREATE", resource = "ROLE")
    public ResponseEntity<PendingActionResponseDTO> createRole(@Valid @RequestBody RoleUtilisateurRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(pendingActionMapper.toDto(roleUtilisateurService.submitCreateRole(
                        requestDTO.getCodeRoleUtilisateur(),
                        requestDTO.getIntituleRole(),
                        requestDTO.getCommentaireMaker()
                )));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing role")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECURITY_PERMISSION_MANAGE')")
    @AuditLog(action = "ROLE_UPDATE", resource = "ROLE")
    public ResponseEntity<PendingActionResponseDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUtilisateurRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(roleUtilisateurService.submitUpdateRole(
                id,
                requestDTO.getCodeRoleUtilisateur(),
                requestDTO.getIntituleRole(),
                requestDTO.getCommentaireMaker()
        )));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SECURITY_PERMISSION_MANAGE')")
    @AuditLog(action = "ROLE_DELETE", resource = "ROLE")
    public ResponseEntity<PendingActionResponseDTO> deleteRole(
            @PathVariable Long id,
            @RequestBody(required = false) RoleUtilisateurRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(pendingActionMapper.toDto(
                roleUtilisateurService.submitDeleteRole(id, requestDTO == null ? null : requestDTO.getCommentaireMaker())
        ));
    }
}
