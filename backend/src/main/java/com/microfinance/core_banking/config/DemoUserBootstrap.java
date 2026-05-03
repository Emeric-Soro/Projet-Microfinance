package com.microfinance.core_banking.config;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo-user", name = "enabled", havingValue = "true")
public class DemoUserBootstrap implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoUserBootstrap.class);

    private final DemoUserProperties demoUserProperties;
    private final AuthSecurityProperties authSecurityProperties;
    private final ClientRepository clientRepository;
    private final StatutClientRepository statutClientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        validateProperties();

        StatutClient statutClient = ensureStatutClient();
        RoleUtilisateur roleUtilisateur = ensureRoleUtilisateur();

        Client client = resolveDemoClient();
        ensureClientReferencesAreConsistent(client);
        client = clientRepository.save(syncClient(client, statutClient));

        Utilisateur utilisateur = resolveDemoUser(client);
        utilisateur = utilisateurRepository.save(syncUtilisateur(utilisateur, client, roleUtilisateur));

        LOGGER.info(
                "Compte de demonstration pret: login={}, codeClient={}, idClient={}, idUser={}, secondFactorEnabled={}",
                utilisateur.getLogin(),
                client.getCodeClient(),
                client.getIdClient(),
                utilisateur.getIdUser(),
                utilisateur.getSecondFacteurActive()
        );
    }

    private void validateProperties() {
        requireText(demoUserProperties.getLogin(), "app.demo-user.login");
        requireText(demoUserProperties.getPassword(), "app.demo-user.password");
        requireText(demoUserProperties.getClientCode(), "app.demo-user.client-code");
        requireText(demoUserProperties.getClientStatus(), "app.demo-user.client-status");
        requireText(demoUserProperties.getRoleCode(), "app.demo-user.role-code");
        requireText(demoUserProperties.getRoleLabel(), "app.demo-user.role-label");
        requireText(demoUserProperties.getNom(), "app.demo-user.nom");
        requireText(demoUserProperties.getPrenom(), "app.demo-user.prenom");
        requireText(demoUserProperties.getEmail(), "app.demo-user.email");
        requireText(demoUserProperties.getTelephone(), "app.demo-user.telephone");

        if (demoUserProperties.getDateNaissance() == null) {
            throw new IllegalStateException("La propriete app.demo-user.date-naissance est obligatoire");
        }
    }

    private void requireText(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("La propriete " + propertyName + " est obligatoire");
        }
    }

    private StatutClient ensureStatutClient() {
        String libelle = demoUserProperties.getClientStatus().trim();
        return statutClientRepository.findByLibelleStatutIgnoreCase(libelle)
                .orElseGet(() -> {
                    StatutClient statutClient = new StatutClient();
                    statutClient.setLibelleStatut(libelle.toUpperCase());
                    statutClient.setDateStatut(LocalDateTime.now());
                    return statutClientRepository.save(statutClient);
                });
    }

    private RoleUtilisateur ensureRoleUtilisateur() {
        String codeRole = demoUserProperties.getRoleCode().trim().toUpperCase();
        String intituleRole = demoUserProperties.getRoleLabel().trim();

        RoleUtilisateur roleUtilisateur = roleUtilisateurRepository.findByCodeRoleUtilisateur(codeRole)
                .orElseGet(RoleUtilisateur::new);

        roleUtilisateur.setCodeRoleUtilisateur(codeRole);
        roleUtilisateur.setIntituleRole(intituleRole);

        if (roleUtilisateur.getPermissions() == null) {
            roleUtilisateur.setPermissions(new HashSet<>());
        }
        if (roleUtilisateur.getUtilisateurs() == null) {
            roleUtilisateur.setUtilisateurs(new HashSet<>());
        }

        return roleUtilisateurRepository.save(roleUtilisateur);
    }

    private Client resolveDemoClient() {
        return utilisateurRepository.findByLogin(demoUserProperties.getLogin().trim())
                .map(Utilisateur::getClient)
                .or(() -> clientRepository.findByCodeClient(demoUserProperties.getClientCode().trim()))
                .or(() -> clientRepository.findByEmail(demoUserProperties.getEmail().trim()))
                .or(() -> clientRepository.findByTelephone(demoUserProperties.getTelephone().trim()))
                .orElseGet(Client::new);
    }

    private void ensureClientReferencesAreConsistent(Client client) {
        assertSameClient(clientRepository.findByCodeClient(demoUserProperties.getClientCode().trim()), client, "code client");
        assertSameClient(clientRepository.findByEmail(demoUserProperties.getEmail().trim()), client, "email");
        assertSameClient(clientRepository.findByTelephone(demoUserProperties.getTelephone().trim()), client, "telephone");
    }

    private void assertSameClient(Optional<Client> existingClient, Client expectedClient, String fieldName) {
        if (existingClient.isEmpty()) {
            return;
        }

        Long expectedId = expectedClient.getIdClient();
        Long existingId = existingClient.get().getIdClient();
        if (!Objects.equals(existingId, expectedId)) {
            throw new IllegalStateException("Conflit sur le compte de demonstration: le " + fieldName + " est deja utilise par un autre client");
        }
    }

    private Client syncClient(Client client, StatutClient statutClient) {
        LocalDate today = LocalDate.now();

        client.setCodeClient(demoUserProperties.getClientCode().trim());
        client.setNom(demoUserProperties.getNom().trim());
        client.setPrenom(demoUserProperties.getPrenom().trim());
        client.setDateNaissance(demoUserProperties.getDateNaissance());
        client.setEmail(demoUserProperties.getEmail().trim().toLowerCase());
        client.setTelephone(demoUserProperties.getTelephone().trim());
        client.setDateInscription(client.getDateInscription() != null ? client.getDateInscription() : today);
        client.setPep(Boolean.FALSE);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setStatutKyc(StatutKycClient.VALIDE);
        client.setDateSoumissionKyc(client.getDateSoumissionKyc() != null ? client.getDateSoumissionKyc() : today);
        client.setDateValidationKyc(client.getDateValidationKyc() != null ? client.getDateValidationKyc() : today);
        client.setCommentaireKyc("Compte de demonstration local");
        client.setValidateurKyc("demo-bootstrap");
        client.setStatutClient(statutClient);

        return client;
    }

    private Utilisateur resolveDemoUser(Client client) {
        return utilisateurRepository.findByLogin(demoUserProperties.getLogin().trim())
                .or(() -> client.getIdClient() == null
                        ? Optional.empty()
                        : utilisateurRepository.findByClient_IdClient(client.getIdClient()))
                .orElseGet(Utilisateur::new);
    }

    private Utilisateur syncUtilisateur(Utilisateur utilisateur, Client client, RoleUtilisateur roleUtilisateur) {
        LocalDateTime now = LocalDateTime.now();

        utilisateur.setClient(client);
        utilisateur.setLogin(demoUserProperties.getLogin().trim());
        utilisateur.setPassword(passwordEncoder.encode(demoUserProperties.getPassword()));
        utilisateur.setActif(Boolean.TRUE);
        utilisateur.setCompteExpireLe(null);
        utilisateur.setCompteVerrouilleJusquAu(null);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setDernierEchecConnexion(null);
        utilisateur.setDerniereConnexionReussie(null);
        utilisateur.setMotDePasseModifieLe(now);
        utilisateur.setIdentifiantsExpirentLe(now.plusDays(authSecurityProperties.getCredentialsValidityDays()));
        utilisateur.setSecondFacteurActive(demoUserProperties.isSecondFactorEnabled());
        utilisateur.setOtpChallengeId(null);
        utilisateur.setOtpHash(null);
        utilisateur.setOtpExpireLe(null);
        utilisateur.setOtpTentativesRestantes(0);

        if (utilisateur.getRoles() == null) {
            utilisateur.setRoles(new HashSet<>());
        }
        utilisateur.getRoles().add(roleUtilisateur);

        return utilisateur;
    }
}
