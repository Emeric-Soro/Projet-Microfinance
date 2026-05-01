package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.config.AuthSecurityProperties;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.HistoriqueMotDePasseUtilisateur;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.HistoriqueMotDePasseUtilisateurRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.service.communication.NotificationService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final HistoriqueMotDePasseUtilisateurRepository historiqueMotDePasseUtilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthSecurityProperties authSecurityProperties;
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;
    private final SecureRandom secureRandom = new SecureRandom();

    public UtilisateurServiceImpl(
            UtilisateurRepository utilisateurRepository,
            ClientRepository clientRepository,
            HistoriqueMotDePasseUtilisateurRepository historiqueMotDePasseUtilisateurRepository,
            RoleUtilisateurRepository roleUtilisateurRepository,
            PasswordEncoder passwordEncoder,
            AuthSecurityProperties authSecurityProperties,
            NotificationService notificationService,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.historiqueMotDePasseUtilisateurRepository = historiqueMotDePasseUtilisateurRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authSecurityProperties = authSecurityProperties;
        this.notificationService = notificationService;
        this.authenticatedUserService = authenticatedUserService;
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
    public Utilisateur revoquerRole(Long idUser, String codeRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        if (codeRole == null || codeRole.isBlank()) {
            throw new IllegalArgumentException("Le code role est obligatoire");
        }

        RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                .orElseThrow(() -> new IllegalArgumentException("Le rôle '" + codeRole + "' n'existe pas."));

        utilisateur.getRoles().remove(role);
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
    @Transactional
    public Utilisateur changerMotDePasse(Long idUser, String motDePasseActuel, String nouveauMotDePasse, String motif) {
        Utilisateur acteur = authenticatedUserService.getCurrentUserOrThrow();
        Utilisateur utilisateur = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable: " + idUser));

        boolean selfService = acteur.getIdUser() != null && acteur.getIdUser().equals(utilisateur.getIdUser());
        boolean administrateur = acteur.getRoles().stream()
                .map(RoleUtilisateur::getCodeRoleUtilisateur)
                .map(String::toUpperCase)
                .anyMatch(role -> role.equals("ADMIN") || role.equals("SUPERVISEUR"));

        if (!selfService && !administrateur) {
            throw new IllegalStateException("Seul l'utilisateur lui-meme ou un administrateur peut changer ce mot de passe");
        }

        if (nouveauMotDePasse == null || nouveauMotDePasse.isBlank()) {
            throw new IllegalArgumentException("Le nouveau mot de passe est obligatoire");
        }
        if (!respectePolitiqueMotDePasse(nouveauMotDePasse)) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir une majuscule, une minuscule, un chiffre et un caractere special");
        }

        if (selfService) {
            if (motDePasseActuel == null || motDePasseActuel.isBlank()) {
                throw new IllegalArgumentException("Le mot de passe actuel est obligatoire");
            }
            if (!passwordEncoder.matches(motDePasseActuel, utilisateur.getPassword())) {
                throw new BadCredentialsException("Le mot de passe actuel est invalide");
            }
        }

        interdireReutilisationMotDePasse(utilisateur, nouveauMotDePasse);
        archiverMotDePasseCourant(utilisateur, motif);

        LocalDateTime maintenant = LocalDateTime.now();
        utilisateur.setPassword(passwordEncoder.encode(nouveauMotDePasse));
        utilisateur.setMotDePasseModifieLe(maintenant);
        utilisateur.setIdentifiantsExpirentLe(maintenant.plusDays(authSecurityProperties.getCredentialsValidityDays()));
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setDernierEchecConnexion(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
        utilisateur.setOtpChallengeId(null);
        utilisateur.setOtpHash(null);
        utilisateur.setOtpExpireLe(null);
        utilisateur.setOtpTentativesRestantes(0);

        return utilisateurRepository.save(utilisateur);
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
            notificationService.envoyerAlerteConnexionSuspecte(utilisateur.getClient().getIdClient());
        }

        utilisateurRepository.save(utilisateur);
    }

    private void reinitialiserEtatConnexion(Utilisateur utilisateur) {
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setDernierEchecConnexion(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
    }

    private void interdireReutilisationMotDePasse(Utilisateur utilisateur, String nouveauMotDePasse) {
        if (passwordEncoder.matches(nouveauMotDePasse, utilisateur.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit etre different du mot de passe courant");
        }

        int profondeurHistorique = Math.max(authSecurityProperties.getPasswordHistoryDepth(), 1);
        List<HistoriqueMotDePasseUtilisateur> historiques = historiqueMotDePasseUtilisateurRepository
                .findByUtilisateur_IdUserOrderByDateChangementDesc(utilisateur.getIdUser());

        historiques.stream()
                .limit(profondeurHistorique)
                .filter(historique -> passwordEncoder.matches(nouveauMotDePasse, historique.getHashMotDePasse()))
                .findFirst()
                .ifPresent(historique -> {
                    throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas reutiliser un mot de passe recent");
                });
    }

    private void archiverMotDePasseCourant(Utilisateur utilisateur, String motif) {
        HistoriqueMotDePasseUtilisateur historique = new HistoriqueMotDePasseUtilisateur();
        historique.setUtilisateur(utilisateur);
        historique.setHashMotDePasse(utilisateur.getPassword());
        historique.setDateChangement(LocalDateTime.now());
        historique.setMotif((motif == null || motif.isBlank()) ? "CHANGEMENT_MOT_DE_PASSE" : motif.trim());
        historiqueMotDePasseUtilisateurRepository.save(historique);
    }

    private boolean respectePolitiqueMotDePasse(String motDePasse) {
        return motDePasse.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,100}$");
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
