package com.soutra.microfinance.service.client;

import com.soutra.microfinance.entity.Utilisateur;

public record AuthenticationWorkflowResult(
        Utilisateur utilisateur,
        boolean otpRequired,
        String challengeId,
        String message
) {
}
