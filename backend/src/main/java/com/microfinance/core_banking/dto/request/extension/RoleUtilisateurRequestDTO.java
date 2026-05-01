package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleUtilisateurRequestDTO {
    @NotBlank(message = "Le code du role est obligatoire")
    private String codeRoleUtilisateur;
    
    @NotBlank(message = "L'intitule du role est obligatoire")
    private String intituleRole;

    @Size(max = 500, message = "Le commentaire maker ne doit pas depasser 500 caracteres")
    private String commentaireMaker;
}
