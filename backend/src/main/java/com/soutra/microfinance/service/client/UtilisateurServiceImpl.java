package com.soutra.microfinance.service.client;

import com.soutra.microfinance.config.AuthSecurityProperties;
import com.soutra.microfinance.config.PasswordResetProperties;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.service.communication.EmailService;
import com.soutra.microfinance.service.communication.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilisateurServiceImpl.class);

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSecurityProperties authSecurityProperties;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final PasswordResetProperties passwordResetProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public UtilisateurServiceImpl(
            UtilisateurRepository utilisateurRepository,
            ClientRepository clientRepository,
            RoleUtilisateurRepository roleUtilisateurRepository,
            PasswordEncoder passwordEncoder,
            AuthSecurityProperties authSecurityProperties,
            NotificationService notificationService,
            EmailService emailService,
            PasswordResetProperties passwordResetProperties
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecurityProperties = authSecurityProperties;
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.passwordResetProperties = passwordResetProperties;
    }

    @Override
    @Transactional
    public Utilisateur creerCompteWeb(
            String codeClient,
            String email,
            LocalDate dateNaissance,
            String motDePasse
    ) {
        if (codeClient == null || codeClient.isBlank()) {
            throw new IllegalArgumentException("Le code client est obligatoire");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (dateNaissance == null) {
            throw new IllegalArgumentException("La date de naissance est obligatoire");
        }
        if (motDePasse == null || motDePasse.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        Client client = clientRepository.findByCodeClient(normaliserCodeClient(codeClient))
                .orElseThrow(() -> new IllegalArgumentException("Les informations d'activation ne correspondent a aucun client"));

        if (!emailCorrespond(email, client) || !dateNaissance.equals(client.getDateNaissance())) {
            throw new IllegalArgumentException("Les informations d'activation ne correspondent a aucun client");
        }
        if (utilisateurRepository.existsByClient_IdClient(client.getIdClient())) {
            throw new IllegalArgumentException("Ce client possede deja un compte web");
        }
        if (client.getTelephone() == null || client.getTelephone().isBlank()) {
            throw new IllegalStateException("Le client doit disposer d'un numero de telephone pour activer le second facteur");
        }

        String login = genererLogin(client);
        if (utilisateurRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Login deja utilise: " + login);
        }

        LocalDateTime maintenant = LocalDateTime.now();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        utilisateur.setLogin(login);
        utilisateur.setPassword(passwordEncoder.encode(motDePasse));
        utilisateur.setActif(Boolean.TRUE);
        utilisateur.setCompteExpireLe(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setDernierEchecConnexion(null);
        utilisateur.setDerniereConnexionReussie(null);
        utilisateur.setMotDePasseModifieLe(maintenant);
        utilisateur.setIdentifiantsExpirentLe(maintenant.plusDays(authSecurityProperties.getCredentialsValidityDays()));
        utilisateur.setSecondFacteurActive(Boolean.TRUE);
        utilisateur.setOtpChallengeId(null);
        utilisateur.setOtpHash(null);
        utilisateur.setOtpExpireLe(null);
        utilisateur.setOtpTentativesRestantes(0);

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public AuthenticationWorkflowResult authentifier(String login, String motDePasseBrut) {
        if (login == null || login.isBlank() || motDePasseBrut == null || motDePasseBrut.isBlank()) {
            throw new IllegalArgumentException("Le login et le mot de passe sont obligatoires");
        }

        Utilisateur utilisateur = utilisateurRepository.findByLogin(login.trim())
                .orElseThrow(() -> new BadCredentialsException("Identifiants invalides"));

        verifierStatutsAuthentification(utilisateur);

        if (!passwordEncoder.matches(motDePasseBrut, utilisateur.getPassword())) {
            enregistrerEchecAuthentification(utilisateur);
            throw new BadCredentialsException("Identifiants invalides");
        }

        reinitialiserEtatConnexion(utilisateur);

        if (!Boolean.TRUE.equals(utilisateur.getSecondFacteurActive())) {
            utilisateur.setDerniereConnexionReussie(LocalDateTime.now());
            Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);
            return new AuthenticationWorkflowResult(
                    utilisateurSauvegarde,
                    false,
                    null,
                    "Authentification reussie"
            );
        }

        String codeOtp = genererCodeOtp();
        String challengeId = genererChallenge();
        utilisateur.setOtpChallengeId(challengeId);
        utilisateur.setOtpHash(passwordEncoder.encode(codeOtp));
        utilisateur.setOtpExpireLe(LocalDateTime.now().plus(authSecurityProperties.getOtpValidity()));
        utilisateur.setOtpTentativesRestantes(authSecurityProperties.getMaxOtpAttempts());
        utilisateurRepository.save(utilisateur);
        notificationService.envoyerCodeAuthentification(utilisateur.getClient().getIdClient(), codeOtp);

        return new AuthenticationWorkflowResult(
                null,
                true,
                challengeId,
                "Un code de verification a ete envoye au client"
        );
    }

    @Override
    @Transactional
    public Utilisateur verifierSecondFacteur(String login, String challengeId, String codeOtp) {
        if (login == null || login.isBlank() || challengeId == null || challengeId.isBlank() || codeOtp == null || codeOtp.isBlank()) {
            throw new IllegalArgumentException("Le login, le challenge et le code OTP sont obligatoires");
        }

        Utilisateur utilisateur = utilisateurRepository.findByLogin(login.trim())
                .orElseThrow(() -> new BadCredentialsException("Code OTP invalide"));

        verifierStatutsAuthentification(utilisateur);

        if (!Boolean.TRUE.equals(utilisateur.getSecondFacteurActive())) {
            throw new IllegalStateException("Le second facteur n'est pas active pour cet utilisateur");
        }

        boolean challengeValide = challengeId.equals(utilisateur.getOtpChallengeId());
        boolean otpNonExpire = utilisateur.getOtpExpireLe() != null && utilisateur.getOtpExpireLe().isAfter(LocalDateTime.now());
        boolean otpValide = utilisateur.getOtpHash() != null && passwordEncoder.matches(codeOtp, utilisateur.getOtpHash());
        if (!challengeValide || !otpNonExpire || !otpValide) {
            enregistrerEchecOtp(utilisateur);
            throw new BadCredentialsException("Code OTP invalide");
        }

        utilisateur.setOtpChallengeId(null);
        utilisateur.setOtpHash(null);
        utilisateur.setOtpExpireLe(null);
        utilisateur.setOtpTentativesRestantes(0);
        utilisateur.setDerniereConnexionReussie(LocalDateTime.now());
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public Utilisateur assignerRole(Long idUser, String codeRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        if (codeRole == null || codeRole.isBlank()) {
            throw new IllegalArgumentException("Le code role est obligatoire");
        }

        RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                .orElseThrow(() -> new IllegalArgumentException("Alerte de sécurité : Le rôle '" + codeRole + "' n'existe pas."));

        utilisateur.getRoles().add(role);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public Utilisateur changerActivation(Long idUser, boolean actif) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        utilisateur.setActif(actif);
        if (!actif) {
            utilisateur.setOtpChallengeId(null);
            utilisateur.setOtpHash(null);
            utilisateur.setOtpExpireLe(null);
            utilisateur.setOtpTentativesRestantes(0);
        }
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public Utilisateur chargerUtilisateurParLogin(String login) {
        return utilisateurRepository.findByLogin(login.trim())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Utilisateur introuvable: " + login));
    }

    @Override
    @Transactional
    public Utilisateur activerOuDesactiver2FA(Long idUser, boolean activer, String codeOtp) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        verifierCodeOtpDisponible(utilisateur);
        if (activer) {
            verifierStatutsAuthentification(utilisateur);
            if (!passwordEncoder.matches(codeOtp, utilisateur.getOtpHash())) {
                throw new IllegalArgumentException("Code OTP invalide");
            }
        } else {
            if (!passwordEncoder.matches(codeOtp, utilisateur.getOtpHash())) {
                throw new IllegalArgumentException("Code OTP invalide pour desactivation");
            }
        }

        utilisateur.setSecondFacteurActive(activer);
        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void verifierCodeOtp(Long idUser, String codeOtp) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        verifierCodeOtpDisponible(utilisateur);
        boolean otpValide = utilisateur.getOtpHash() != null
                && passwordEncoder.matches(codeOtp, utilisateur.getOtpHash())
                && utilisateur.getOtpExpireLe() != null
                && utilisateur.getOtpExpireLe().isAfter(LocalDateTime.now());

        if (!otpValide) {
            enregistrerEchecOtp(utilisateur);
            throw new IllegalArgumentException("Code OTP invalide ou expire");
        }
    }

    private void verifierCodeOtpDisponible(Utilisateur utilisateur) {
        if (utilisateur.getOtpHash() == null || utilisateur.getOtpExpireLe() == null) {
            throw new IllegalArgumentException("Aucun code OTP actif. Demandez d'abord un nouveau code.");
        }
    }

    @Override
    @Transactional
    public void changerMotDePasse(Long idUser, String ancienMotDePasse, String nouveauMotDePasse) {
        if (ancienMotDePasse == null || ancienMotDePasse.isBlank()) {
            throw new IllegalArgumentException("L'ancien mot de passe est obligatoire");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caracteres");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(authSecurityProperties.getCredentialsValidityDays()));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public Utilisateur modifierCompteWeb(Long idUser, String email, String motDePasse) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (motDePasse == null || motDePasse.length() < 8) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caracteres");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(utilisateur.getLogin())) {
            String nouveauLogin = email.trim().toLowerCase();
            if (utilisateurRepository.existsByLogin(nouveauLogin)) {
                throw new IllegalArgumentException("Cet email est deja utilise par un autre compte");
            }
            utilisateur.setLogin(nouveauLogin);
        }

        utilisateur.setPassword(passwordEncoder.encode(motDePasse));
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(authSecurityProperties.getCredentialsValidityDays()));

        return utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void demanderResetMotDePasse(String loginOuEmail) {
        if (loginOuEmail == null || loginOuEmail.isBlank()) {
            return;
        }

        String identifiant = loginOuEmail.trim();
        Optional<Utilisateur> utilisateurOpt = trouverUtilisateurParLoginOuEmail(identifiant);

        if (utilisateurOpt.isEmpty()) {
            LOGGER.info("Demande de reset recue pour un identifiant inconnu : {}", identifiant);
            return;
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        String tokenClair = UUID.randomUUID().toString();
        utilisateur.setResetTokenHash(passwordEncoder.encode(tokenClair));
        utilisateur.setResetTokenExpireLe(LocalDateTime.now().plus(passwordResetProperties.getTokenValidity()));
        utilisateurRepository.save(utilisateur);

        emailService.envoyerResetMotDePasse(utilisateur, tokenClair);
    }

    @Override
    @Transactional
    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Le token de reinitialisation est obligatoire");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caracteres");
        }

        // Recherche de l'utilisateur par token. On ne peut pas hasher le token recu
        // et comparer directement (BCrypt produit un hash non-deterministe avec salt).
        // Strategie : on recupere tous les utilisateurs ayant un reset_token_hash non null,
        // et on compare avec matches() cote service.
        LocalDateTime maintenant = LocalDateTime.now();
        Utilisateur utilisateurTrouve = null;
        for (Utilisateur u : utilisateurRepository.findAll()) {
            if (u.getResetTokenHash() == null || u.getResetTokenExpireLe() == null) {
                continue;
            }
            if (u.getResetTokenExpireLe().isBefore(maintenant)) {
                continue;
            }
            if (passwordEncoder.matches(token, u.getResetTokenHash())) {
                utilisateurTrouve = u;
                break;
            }
        }

        if (utilisateurTrouve == null) {
            throw new IllegalArgumentException("Token invalide ou expire");
        }

        LocalDateTime maintenant2 = LocalDateTime.now();
        utilisateurTrouve.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurTrouve.setResetTokenHash(null);
        utilisateurTrouve.setResetTokenExpireLe(null);
        utilisateurTrouve.setMotDePasseModifieLe(maintenant2);
        utilisateurTrouve.setLastPasswordChange(maintenant2);
        utilisateurTrouve.setIdentifiantsExpirentLe(maintenant2.plusDays(authSecurityProperties.getCredentialsValidityDays()));
        utilisateurTrouve.setNombreEchecsConnexion(0);
        utilisateurTrouve.setCompteVerrouilleJusquAu(null);
        utilisateurRepository.save(utilisateurTrouve);
    }

    private Optional<Utilisateur> trouverUtilisateurParLoginOuEmail(String identifiant) {
        Optional<Utilisateur> parLogin = utilisateurRepository.findByLogin(identifiant);
        if (parLogin.isPresent()) {
            return parLogin;
        }
        if (identifiant.contains("@")) {
            return utilisateurRepository.findByClient_EmailIgnoreCase(identifiant);
        }
        return Optional.empty();
    }

    private void verifierStatutsAuthentification(Utilisateur utilisateur) {
        if (!utilisateur.isEnabled()) {
            throw new DisabledException("Compte desactive");
        }
        if (!utilisateur.isAccountNonExpired()) {
            throw new AccountExpiredException("Compte expire");
        }
        if (!utilisateur.isAccountNonLocked()) {
            throw new LockedException("Compte verrouille");
        }
        if (!utilisateur.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Identifiants expires");
        }
    }

    private void enregistrerEchecAuthentification(Utilisateur utilisateur) {
        LocalDateTime maintenant = LocalDateTime.now();
        int echecs = utilisateur.getNombreEchecsConnexion() == null ? 0 : utilisateur.getNombreEchecsConnexion();
        utilisateur.setNombreEchecsConnexion(echecs + 1);
        utilisateur.setDernierEchecConnexion(maintenant);
        utilisateur.setOtpChallengeId(null);
        utilisateur.setOtpHash(null);
        utilisateur.setOtpExpireLe(null);
        utilisateur.setOtpTentativesRestantes(0);

        if (utilisateur.getNombreEchecsConnexion() >= authSecurityProperties.getMaxFailedAttempts()) {
            utilisateur.setCompteVerrouilleJusquAu(maintenant.plus(authSecurityProperties.getLockDuration()));
            utilisateur.setNombreEchecsConnexion(0);
            LOGGER.warn("Compte utilisateur {} verrouille apres echecs de connexion", utilisateur.getLogin());
            notificationService.envoyerAlerteConnexionSuspecte(utilisateur.getClient().getIdClient());
        }

        utilisateurRepository.save(utilisateur);
    }

    private void enregistrerEchecOtp(Utilisateur utilisateur) {
        LocalDateTime maintenant = LocalDateTime.now();
        int tentativesRestantes = utilisateur.getOtpTentativesRestantes() == null
                ? 0
                : utilisateur.getOtpTentativesRestantes() - 1;

        utilisateur.setOtpTentativesRestantes(Math.max(tentativesRestantes, 0));
        utilisateur.setDernierEchecConnexion(maintenant);

        if (tentativesRestantes <= 0) {
            utilisateur.setCompteVerrouilleJusquAu(maintenant.plus(authSecurityProperties.getLockDuration()));
            utilisateur.setOtpChallengeId(null);
            utilisateur.setOtpHash(null);
            utilisateur.setOtpExpireLe(null);
            utilisateur.setOtpTentativesRestantes(0);
            LOGGER.warn("Compte utilisateur {} verrouille apres echecs OTP", utilisateur.getLogin());
            notificationService.envoyerAlerteConnexionSuspecte(utilisateur.getClient().getIdClient());
        }

        utilisateurRepository.save(utilisateur);
    }

    private void reinitialiserEtatConnexion(Utilisateur utilisateur) {
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setDernierEchecConnexion(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
    }

    private String genererLogin(Client client) {
        if (client.getEmail() != null && !client.getEmail().isBlank()) {
            return client.getEmail().trim().toLowerCase();
        }
        return client.getCodeClient().trim().toLowerCase();
    }

    private String normaliserCodeClient(String codeClient) {
        return codeClient.trim().toUpperCase();
    }

    private boolean emailCorrespond(String email, Client client) {
        return client.getEmail() != null
                && client.getEmail().trim().equalsIgnoreCase(email.trim());
    }

    private String genererChallenge() {
        return java.util.UUID.randomUUID().toString();
    }

    private String genererCodeOtp() {
        int borne = (int) Math.pow(10, authSecurityProperties.getOtpLength());
        int minimum = borne / 10;
        return String.format("%0" + authSecurityProperties.getOtpLength() + "d", secureRandom.nextInt(minimum, borne));
    }
}
