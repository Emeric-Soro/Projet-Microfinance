package com.soutra.microfinance.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String token;
    private String refreshToken;
    private UtilisateurResponseDTO utilisateur;
    private AuthenticationStepStatus statutAuthentification;
    private Boolean otpRequis;
    private String challengeId;
    private String message;
}
