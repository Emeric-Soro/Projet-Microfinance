package com.soutra.microfinance.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifierRoleRequestDTO {

    @NotBlank(message = "Le nouveau code du role est obligatoire")
    @Size(max = 50, message = "Le code ne doit pas depasser 50 caracteres")
    private String codeRole;

    @NotBlank(message = "Le nouvel intitule du role est obligatoire")
    @Size(max = 100, message = "L'intitule ne doit pas depasser 100 caracteres")
    private String intitule;
}
