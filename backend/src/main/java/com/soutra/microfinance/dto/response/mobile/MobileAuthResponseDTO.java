package com.soutra.microfinance.dto.response.mobile;

public record MobileAuthResponseDTO(
        String token,
        String refreshToken,
        String statutAuthentification,
        String challengeId,
        MobileUtilisateurDTO utilisateur
) {}
