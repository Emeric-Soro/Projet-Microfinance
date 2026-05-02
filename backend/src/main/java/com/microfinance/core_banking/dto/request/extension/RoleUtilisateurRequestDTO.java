package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Requête de création d'un rôle utilisateur")
public class RoleUtilisateurRequestDTO {
    @NotBlank(message = "Le code du role est obligatoire")
    @Schema(description = "Code du rôle (obligatoire)", example = "GUICHETIER")
    private String codeRoleUtilisateur;
    
    @NotBlank(message = "L'intitule du role est obligatoire")
    @Schema(description = "Intitulé du rôle (obligatoire)", example = "Guichetier")
    private String intituleRole;

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Création du rôle guichetier")
    private String commentaireMaker;
}
