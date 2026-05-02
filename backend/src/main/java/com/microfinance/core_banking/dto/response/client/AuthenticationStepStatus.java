package com.microfinance.core_banking.dto.response.client;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut de l'étape d'authentification")
public enum AuthenticationStepStatus {
    AUTHENTIFIE,
    OTP_REQUIS
}
