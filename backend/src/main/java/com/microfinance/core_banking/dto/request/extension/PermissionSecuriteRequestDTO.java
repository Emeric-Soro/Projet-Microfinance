package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class PermissionSecuriteRequestDTO {

    @NotBlank(message = "Le code de permission est obligatoire")
    @Size(max = 100, message = "Le code de permission ne doit pas depasser 100 caracteres")
    private String codePermission;

    @NotBlank(message = "Le libelle de permission est obligatoire")
    @Size(max = 150, message = "Le libelle de permission ne doit pas depasser 150 caracteres")
    private String libellePermission;

    @NotBlank(message = "Le module de securite est obligatoire")
    @Size(max = 60, message = "Le module de securite ne doit pas depasser 60 caracteres")
    private String moduleCode;

    @Size(max = 500, message = "La description ne doit pas depasser 500 caracteres")
    private String descriptionPermission;

    private Boolean actif;

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    private String commentaireMaker;

    public static PermissionSecuriteRequestDTO fromMap(Map<String, Object> payload) {
        PermissionSecuriteRequestDTO dto = new PermissionSecuriteRequestDTO();
        dto.setCodePermission(payload.get("codePermission") == null ? null : payload.get("codePermission").toString().trim().toUpperCase());
        dto.setLibellePermission(payload.get("libellePermission") == null ? null : payload.get("libellePermission").toString().trim());
        dto.setModuleCode(payload.get("moduleCode") == null ? null : payload.get("moduleCode").toString().trim().toUpperCase());
        dto.setDescriptionPermission(payload.get("descriptionPermission") == null ? null : payload.get("descriptionPermission").toString().trim());
        dto.setActif(payload.get("actif") == null ? Boolean.TRUE : Boolean.parseBoolean(payload.get("actif").toString()));
        dto.setCommentaireMaker(payload.get("commentaireMaker") == null ? null : payload.get("commentaireMaker").toString().trim());
        return dto;
    }
}
