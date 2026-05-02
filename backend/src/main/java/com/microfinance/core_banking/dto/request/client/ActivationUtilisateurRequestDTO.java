package com.microfinance.core_banking.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête d'activation ou désactivation d'un utilisateur")
public class ActivationUtilisateurRequestDTO {

    @NotNull(message = "Le statut d'activation est obligatoire")
    @Schema(description = "Statut d'activation (obligatoire)", example = "true")
    private Boolean actif;
}
