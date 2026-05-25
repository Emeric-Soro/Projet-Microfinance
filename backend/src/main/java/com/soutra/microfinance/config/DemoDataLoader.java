package com.soutra.microfinance.config;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.NiveauRisqueClient;
import com.soutra.microfinance.entity.StatutKycClient;
import com.soutra.microfinance.entity.TypePieceIdentite;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.StatutClientRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final StatutClientRepository statutClientRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataLoader(
            ClientRepository clientRepository,
            UtilisateurRepository utilisateurRepository,
            RoleUtilisateurRepository roleUtilisateurRepository,
            StatutClientRepository statutClientRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.clientRepository = clientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.statutClientRepository = statutClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("DemoDataLoader: checking if demo data is needed... client count = {}", clientRepository.count());
        if (clientRepository.count() > 0) {
            log.info("DemoDataLoader: clients already exist, skipping.");
            return;
        }

        log.info("DemoDataLoader: inserting demo client and user...");

        StatutClient statutActif = statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")
                .orElseThrow(() -> new IllegalStateException("StatutClient ACTIF not found in reference data. Did reference-data.sql run?"));

        Client client = new Client();
        client.setCodeClient("CLI-DEMO-0001");
        client.setNom("Demo");
        client.setPrenom("Admin");
        client.setDateNaissance(LocalDate.of(1990, 1, 1));
        client.setAdresse("123 Rue Demo");
        client.setTelephone("700000001");
        client.setEmail("demo.admin@microfin.local");
        client.setTypePieceIdentite(TypePieceIdentite.CNI);
        client.setNumeroPieceIdentite("CNI-DEMO-0001");
        client.setPep(false);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setStatutKyc(StatutKycClient.VALIDE);
        client.setDateInscription(LocalDate.now());

        client.setStatutClient(statutActif);

        client = clientRepository.save(client);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        utilisateur.setLogin("demo.admin");
        utilisateur.setPassword(passwordEncoder.encode("Demo@12345"));
        utilisateur.setActif(true);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setSecondFacteurActive(false);
        utilisateur.setOtpTentativesRestantes(0);
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90));

        utilisateur = utilisateurRepository.save(utilisateur);

        RoleUtilisateur roleAdmin = roleUtilisateurRepository.findByCodeRoleUtilisateur("ADMIN")
                .orElse(null);
        if (roleAdmin != null) {
            utilisateur.getRoles().add(roleAdmin);
            utilisateurRepository.save(utilisateur);
        }

        log.info("DemoDataLoader: demo data inserted successfully.");
    }
}
