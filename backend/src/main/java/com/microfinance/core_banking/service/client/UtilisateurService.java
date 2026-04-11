package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurService {

    Utilisateur creerCompteWeb(Long idClient, String motDePasse);

    Optional<Utilisateur> authentifier(String login, String motDePasseBrut);

    Utilisateur assignerRole(Long idUser, String codeRole);
}
