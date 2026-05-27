package com.soutra.microfinance.config;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.compte.CompteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accountAccessSecurity")
public class AccountAccessSecurity {

    private final CompteRepository compteRepository;

    public AccountAccessSecurity(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    public boolean canAccessAccount(Authentication authentication, String numCompte) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        if (!(authentication.getPrincipal() instanceof Utilisateur utilisateur)) {
            return false;
        }
        Client client = utilisateur.getClient();
        if (client == null || client.getIdClient() == null || numCompte == null || numCompte.isBlank()) {
            return false;
        }
        return compteRepository.existsByNumCompteAndClient_IdClient(numCompte, client.getIdClient());
    }
}
