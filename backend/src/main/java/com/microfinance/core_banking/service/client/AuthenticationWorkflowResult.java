package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Utilisateur;

public record AuthenticationWorkflowResult(
        Utilisateur utilisateur,
        boolean otpRequired,
        String challengeId,
        String message
) {
}
