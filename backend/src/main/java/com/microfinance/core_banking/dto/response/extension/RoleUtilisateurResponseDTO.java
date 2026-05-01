package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

import java.util.Set;

@Data
public class RoleUtilisateurResponseDTO {
    private Long idRole;
    private String codeRoleUtilisateur;
    private String intituleRole;
    private Set<String> permissions;
}
