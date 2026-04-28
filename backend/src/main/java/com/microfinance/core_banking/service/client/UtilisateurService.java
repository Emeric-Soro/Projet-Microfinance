package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Utilisateur;

import java.time.LocalDate;

public interface UtilisateurService {

    Utilisateur creerCompteWeb(String codeClient, String email, LocalDate dateNaissance, String motDePasse);

    AuthenticationWorkflowResult authentifier(String login, String motDePasseBrut);

    Utilisateur verifierSecondFacteur(String login, String challengeId, String codeOtp);

    Utilisateur assignerRole(Long idUser, String codeRole);

    Utilisateur changerActivation(Long idUser, boolean actif);
}
