package com.microfinance.core_banking.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête d'authentification (login) d'un utilisateur")
public class LoginRequestDTO {

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 100, message = "Le login ne doit pas depasser 100 caracteres")
    @Schema(description = "Nom d'utilisateur ou email (obligatoire, max 100 caractères)", example = "jean.dupont")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(max = 100, message = "Le mot de passe ne doit pas depasser 100 caracteres")
    @Schema(description = "Mot de passe (obligatoire, max 100 caractères)", example = "********")
    private String motDePasse;
}
