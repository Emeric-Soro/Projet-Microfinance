package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Requête de modification des permissions d'un rôle")
public class RolePermissionChangeRequestDTO {

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    @Schema(description = "Commentaire (optionnel, max 500 caractères)", example = "Modification des permissions")
    private String commentaireMaker;
}
