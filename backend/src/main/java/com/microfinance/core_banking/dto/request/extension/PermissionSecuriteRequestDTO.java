package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Requête de création d'une permission de sécurité")
public class PermissionSecuriteRequestDTO {

    @NotBlank(message = "Le code de permission est obligatoire")
    @Size(max = 100, message = "Le code de permission ne doit pas depasser 100 caracteres")
    @Schema(description = "Code de la permission (obligatoire, max 100 caractères)", example = "PERM_CREER_CLIENT")
    private String codePermission;

    @NotBlank(message = "Le libelle de permission est obligatoire")
    @Size(max = 150, message = "Le libelle de permission ne doit pas depasser 150 caracteres")
    @Schema(description = "Libellé de la permission (obligatoire, max 150 caractères)", example = "Créer un client")
    private String libellePermission;

    @NotBlank(message = "Le module de securite est obligatoire")
    @Size(max = 60, message = "Le module de securite ne doit pas depasser 60 caracteres")
    @Schema(description = "Code du module de sécurité (obligatoire, max 60 caractères)", example = "MOD_CLIENT")
    private String moduleCode;

    @Size(max = 500, message = "La description ne doit pas depasser 500 caracteres")
    @Schema(description = "Description de la permission (optionnel, max 500 caractères)", example = "Permission de créer un nouveau client")
    private String descriptionPermission;

    @Schema(description = "Permission active (optionnel)", example = "true")
    private Boolean actif;

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Création permission")
    private String commentaireMaker;

    public static PermissionSecuriteRequestDTO fromMap(java.util.Map<String, Object> payload) {
        PermissionSecuriteRequestDTO dto = new PermissionSecuriteRequestDTO();
        dto.setCodePermission((String) payload.get("codePermission"));
        dto.setLibellePermission((String) payload.get("libellePermission"));
        dto.setModuleCode((String) payload.get("moduleCode"));
        dto.setDescriptionPermission((String) payload.get("descriptionPermission"));
        if (payload.get("actif") != null) dto.setActif(Boolean.parseBoolean(payload.get("actif").toString()));
        dto.setCommentaireMaker((String) payload.get("commentaireMaker"));
        return dto;
    }
}
