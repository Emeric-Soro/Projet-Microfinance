package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Rôle utilisateur avec ses permissions")
public class RoleUtilisateurResponseDTO {
    @Schema(description = "Identifiant unique du rôle", example = "1")
    private Long idRole;

    @Schema(description = "Code du rôle", example = "ADMIN")
    private String codeRoleUtilisateur;

    @Schema(description = "Intitulé du rôle", example = "Administrateur système")
    private String intituleRole;

    @Schema(description = "Ensemble des permissions associées", example = "[\"CLIENT_CREATE\",\"CLIENT_READ\",\"CLIENT_UPDATE\"]")
    private Set<String> permissions;
}
