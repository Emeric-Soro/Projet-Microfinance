package com.soutra.microfinance.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de confirmation de reinitialisation de mot de passe.
 * Le token recu par email est valide une seule fois et expire apres 30 minutes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReinitialiserMotDePasseRequestDTO {

    @NotBlank(message = "Le token de reinitialisation est obligatoire")
    private String token;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, max = 128, message = "Le nouveau mot de passe doit contenir entre 8 et 128 caracteres")
    private String nouveauMotDePasse;

    @NotBlank(message = "La confirmation du nouveau mot de passe est obligatoire")
    private String confirmationMotDePasse;
}
