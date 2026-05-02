package com.microfinance.core_banking.dto.response.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse d'authentification contenant le token et les informations utilisateur")
public class AuthenticationResponseDTO {
    @Schema(description = "Token JWT d'authentification", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Informations de l'utilisateur authentifié")
    private UtilisateurResponseDTO utilisateur;

    @Schema(description = "Statut de l'étape d'authentification", example = "AUTHENTIFIE")
    private AuthenticationStepStatus statutAuthentification;

    @Schema(description = "Indique si un code OTP est requis pour finaliser l'authentification", example = "false")
    private Boolean otpRequis;

    @Schema(description = "Identifiant du défi OTP en cours", example = "challenge_abc123")
    private String challengeId;

    @Schema(description = "Message d'information sur le statut de l'authentification", example = "Authentification réussie")
    private String message;
}
