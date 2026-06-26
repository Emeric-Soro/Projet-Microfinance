package com.soutra.microfinance.service.client;

import com.soutra.microfinance.entity.Utilisateur;

import java.time.LocalDate;

public interface UtilisateurService {

    Utilisateur creerCompteWeb(String codeClient, String email, LocalDate dateNaissance, String motDePasse);

    Utilisateur creerCollaborateur(com.soutra.microfinance.dto.request.client.CreationCollaborateurRequestDTO requestDTO);

    AuthenticationWorkflowResult authentifier(String login, String motDePasseBrut);

    Utilisateur verifierSecondFacteur(String login, String challengeId, String codeOtp);

    Utilisateur assignerRole(Long idUser, String codeRole);

    Utilisateur changerActivation(Long idUser, boolean actif);

    Utilisateur chargerUtilisateurParLogin(String login);

    Utilisateur activerOuDesactiver2FA(Long idUser, boolean activer, String codeOtp);

    void verifierCodeOtp(Long idUser, String codeOtp);

    void changerMotDePasse(Long idUser, String ancienMotDePasse, String nouveauMotDePasse);

    Utilisateur modifierCompteWeb(Long idUser, String email, String motDePasse);

    /**
     * Demande la reinitialisation du mot de passe pour un login ou email donne.
     * Genere un token, le persiste (hashe) avec une date d'expiration, et envoie un email.
     * <p>Retourne toujours silencieusement, que le compte existe ou non (anti-enumeration).
     *
     * @param loginOuEmail le login ou l'email du compte
     */
    void demanderResetMotDePasse(String loginOuEmail);

    /**
     * Reinitialise le mot de passe a partir d'un token de reinitialisation valide.
     *
     * @param token          le token recu par email (en clair)
     * @param nouveauMotDePasse le nouveau mot de passe en clair
     * @throws IllegalArgumentException si le token est invalide, expire, ou si le mot de passe ne respecte pas la politique
     */
    void reinitialiserMotDePasse(String token, String nouveauMotDePasse);
}
