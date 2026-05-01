package com.microfinance.core_banking.dto.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationOtpRequestDTO {

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 100, message = "Le login ne doit pas depasser 100 caracteres")
    private String login;

    @NotBlank(message = "Le challenge d'authentification est obligatoire")
    @Size(max = 80, message = "Le challenge d'authentification ne doit pas depasser 80 caracteres")
    private String challengeId;

    @NotBlank(message = "Le code OTP est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code OTP doit contenir exactement 6 chiffres")
    private String codeOtp;
}
