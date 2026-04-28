package com.microfinance.core_banking.dto.request.client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivationUtilisateurRequestDTO {

    @NotNull(message = "Le statut d'activation est obligatoire")
    private Boolean actif;
}
