package com.soutra.microfinance.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de demande de reinitialisation de mot de passe oublie.
 * Le client fournit son login OU son email. Pour des raisons de securite,
 * l'API repond systematiquement 204, qu'un compte correspondant existe ou non.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotDePasseOublieRequestDTO {

    @NotBlank(message = "Le login ou l'email est obligatoire")
    @Size(min = 3, max = 255, message = "Le login ou l'email doit contenir entre 3 et 255 caracteres")
    private String loginOuEmail;
}
