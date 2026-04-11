package com.microfinance.core_banking.dto.request.client;

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
public class LoginRequestDTO {

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 100, message = "Le login ne doit pas depasser 100 caracteres")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(max = 100, message = "Le mot de passe ne doit pas depasser 100 caracteres")
    private String motDePasse;
}
