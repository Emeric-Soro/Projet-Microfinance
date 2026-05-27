package com.soutra.microfinance.service.client;

import com.soutra.microfinance.entity.Utilisateur;

import java.time.LocalDate;

public interface UtilisateurService {

    Utilisateur creerCompteWeb(String codeClient, String email, LocalDate dateNaissance, String motDePasse);

    AuthenticationWorkflowResult authentifier(String login, String motDePasseBrut);

    Utilisateur verifierSecondFacteur(String login, String challengeId, String codeOtp);

    Utilisateur assignerRole(Long idUser, String codeRole);

    Utilisateur changerActivation(Long idUser, boolean actif);
}
