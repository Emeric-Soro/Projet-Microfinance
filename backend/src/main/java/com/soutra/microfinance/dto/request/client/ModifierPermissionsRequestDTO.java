package com.soutra.microfinance.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModifierPermissionsRequestDTO {

    @NotBlank(message = "Le code du role est obligatoire")
    private String codeRole;

    @NotEmpty(message = "Au moins une permission est requise")
    private Set<String> permissions;
}
