package com.microfinance.core_banking.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Requête de changement de mot de passe")
public class ChangementMotDePasseRequestDTO {

    @Size(max = 100, message = "Le mot de passe actuel ne doit pas depasser 100 caracteres")
    @Schema(description = "Mot de passe actuel (optionnel, max 100 caractères)", example = "********")
    private String motDePasseActuel;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, max = 100, message = "Le nouveau mot de passe doit contenir entre 8 et 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,100}$",
            message = "Le nouveau mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractere special"
    )
    @Schema(description = "Nouveau mot de passe (obligatoire, 8-100 caractères, doit contenir majuscule, minuscule, chiffre et caractère spécial)", example = "********")
    private String nouveauMotDePasse;

    @Size(max = 255, message = "Le motif ne doit pas depasser 255 caracteres")
    @Schema(description = "Motif du changement (optionnel, max 255 caractères)", example = "Changement périodique obligatoire")
    private String motif;
}
