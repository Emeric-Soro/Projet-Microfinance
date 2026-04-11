package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;

    public UtilisateurServiceImpl(
            UtilisateurRepository utilisateurRepository,
            ClientRepository clientRepository,
            RoleUtilisateurRepository roleUtilisateurRepository
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
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
        utilisateur.setPassword(hashPassword(motDePasse));

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Utilisateur> authentifier(String login, String motDePasseBrut) {
        if (login == null || login.isBlank() || motDePasseBrut == null || motDePasseBrut.isBlank()) {
            return Optional.empty();
        }

        return utilisateurRepository.findByLogin(login)
                .filter(u -> u.getPassword().equals(hashPassword(motDePasseBrut)));
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

    // TODO: remplacer par BCrypt avec Spring Security lors de l'integration du module securite.
    private String hashPassword(String motDePasse) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(motDePasse.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithme de hash indisponible", e);
        }
    }
}