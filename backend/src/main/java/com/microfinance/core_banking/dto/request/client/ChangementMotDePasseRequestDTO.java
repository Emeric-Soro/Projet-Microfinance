package com.microfinance.core_banking.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangementMotDePasseRequestDTO {

    @Size(max = 100, message = "Le mot de passe actuel ne doit pas depasser 100 caracteres")
    private String motDePasseActuel;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, max = 100, message = "Le nouveau mot de passe doit contenir entre 8 et 100 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,100}$",
            message = "Le nouveau mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractere special"
    )
    private String nouveauMotDePasse;

    @Size(max = 255, message = "Le motif ne doit pas depasser 255 caracteres")
    private String motif;
}
