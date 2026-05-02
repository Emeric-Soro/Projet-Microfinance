package com.microfinance.core_banking.dto.request.client;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Requête de vérification du code OTP pour l'authentification")
public class VerificationOtpRequestDTO {

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 100, message = "Le login ne doit pas depasser 100 caracteres")
    @Schema(description = "Login de l'utilisateur (obligatoire, max 100 caractères)", example = "jean.dupont")
    private String login;

    @NotBlank(message = "Le challenge d'authentification est obligatoire")
    @Size(max = 80, message = "Le challenge d'authentification ne doit pas depasser 80 caracteres")
    @Schema(description = "Identifiant du challenge d'authentification (obligatoire, max 80 caractères)", example = "CHAL-20260401-ABC123")
    private String challengeId;

    @NotBlank(message = "Le code OTP est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code OTP doit contenir exactement 6 chiffres")
    @Schema(description = "Code OTP à 6 chiffres (obligatoire)", example = "123456")
    private String codeOtp;
}
