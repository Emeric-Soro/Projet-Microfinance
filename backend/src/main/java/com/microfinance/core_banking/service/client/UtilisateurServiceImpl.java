package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    // Encodeur centralise fourni par ApplicationConfig (BCrypt).
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(
            UtilisateurRepository utilisateurRepository,
            ClientRepository clientRepository,
            RoleUtilisateurRepository roleUtilisateurRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Utilisateur creerCompteWeb(Long idClient, String motDePasse) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        if (utilisateurRepository.existsByClient_IdClient(idClient)) {
            throw new IllegalArgumentException("Ce client possede deja un compte web");
        }

        String login = genererLogin(client);
        if (utilisateurRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Login deja utilise: " + login);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        utilisateur.setLogin(login);
        // Le mot de passe est encode avant stockage pour ne jamais persister de secret en clair.
        utilisateur.setPassword(passwordEncoder.encode(motDePasse));

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> authentifier(String login, String motDePasseBrut) {
        if (login == null || login.isBlank() || motDePasseBrut == null || motDePasseBrut.isBlank()) {
            return Optional.empty();
        }

        return utilisateurRepository.findByLogin(login)
                // Compare le mot de passe brut avec le hash stocke via l'encodeur Spring Security.
                .filter(u -> passwordEncoder.matches(motDePasseBrut, u.getPassword()));
    }

    @Override
    @Transactional
    public Utilisateur assignerRole(Long idUser, String codeRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        if (codeRole == null || codeRole.isBlank()) {
            throw new IllegalArgumentException("Le code role est obligatoire");
        }

        // Récupération stricte : on refuse d'inventer un rôle de sécurité !
        RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                .orElseThrow(() -> new IllegalArgumentException("Alerte de sécurité : Le rôle '" + codeRole + "' n'existe pas."));

        utilisateur.getRoles().add(role);
        return utilisateurRepository.save(utilisateur);
    }

    private String genererLogin(Client client) {
        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            return client.getEmail().trim().toLowerCase();
        }
        return client.getCodeClient().trim().toLowerCase();
    }
}