package com.microfinance.core_banking.dto.response.client;

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
    private UtilisateurResponseDTO utilisateur;
    private AuthenticationStepStatus statutAuthentification;
    private Boolean otpRequis;
    private String challengeId;
    private String message;
}
