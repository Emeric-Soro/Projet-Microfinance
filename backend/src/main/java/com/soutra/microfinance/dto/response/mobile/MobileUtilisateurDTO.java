package com.soutra.microfinance.dto.response.mobile;

public record MobileUtilisateurDTO(
        Long idUser,
        String login,
        String nom,
        String prenom,
        String telephone,
        String email
) {}
