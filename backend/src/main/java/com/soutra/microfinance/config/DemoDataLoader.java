package com.soutra.microfinance.config;

import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeCompte;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.config.DemoDataSeeds.DemoUserSeed;
import com.soutra.microfinance.config.DemoDataSeeds.TransactionSeed;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.compte.StatutCompteRepository;
import com.soutra.microfinance.repository.compte.TypeCompteRepository;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.StatutClientRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.repository.operation.LigneEcritureRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import com.soutra.microfinance.repository.operation.TypeTransactionRepository;
import com.soutra.microfinance.repository.parametrage.AgenceRepository;
import com.soutra.microfinance.repository.parametrage.ProduitEpargneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final ClientRepository clientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final StatutClientRepository statutClientRepository;
    private final CompteRepository compteRepository;
    private final TypeCompteRepository typeCompteRepository;
    private final StatutCompteRepository statutCompteRepository;
    private final AgenceRepository agenceRepository;
    private final ProduitEpargneRepository produitEpargneRepository;
    private final TypeTransactionRepository typeTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final LigneEcritureRepository ligneEcritureRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedOnStartup;

    public DemoDataLoader(
            ClientRepository clientRepository,
            UtilisateurRepository utilisateurRepository,
            RoleUtilisateurRepository roleUtilisateurRepository,
            StatutClientRepository statutClientRepository,
            CompteRepository compteRepository,
            TypeCompteRepository typeCompteRepository,
            StatutCompteRepository statutCompteRepository,
            AgenceRepository agenceRepository,
            ProduitEpargneRepository produitEpargneRepository,
            TypeTransactionRepository typeTransactionRepository,
            TransactionRepository transactionRepository,
            LigneEcritureRepository ligneEcritureRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo-data.seed-on-startup:true}") boolean seedOnStartup
    ) {
        this.clientRepository = clientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.roleUtilisateurRepository = roleUtilisateurRepository;
        this.statutClientRepository = statutClientRepository;
        this.compteRepository = compteRepository;
        this.typeCompteRepository = typeCompteRepository;
        this.statutCompteRepository = statutCompteRepository;
        this.agenceRepository = agenceRepository;
        this.produitEpargneRepository = produitEpargneRepository;
        this.typeTransactionRepository = typeTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.ligneEcritureRepository = ligneEcritureRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedOnStartup = seedOnStartup;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedOnStartup) {
            log.info("DemoDataLoader: demo data seeding disabled by configuration.");
            return;
        }

        log.info("DemoDataLoader: seeding Ivorian + DRC demo data...");

        StatutClient statutActif = statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")
                .orElseThrow(() -> new IllegalStateException("StatutClient ACTIF not found in reference data. Did reference-data.sql run?"));
        Agence agencePrincipale = agenceRepository.findByCodeAgence("AG-001")
                .orElseThrow(() -> new IllegalStateException("Agence AG-001 not found in reference data. Did reference-data.sql run?"));
        TypeCompte typeEpargne = typeCompteRepository.findByLibelleIgnoreCase("EPARGNE")
                .orElseThrow(() -> new IllegalStateException("TypeCompte EPARGNE not found in reference data. Did reference-data.sql run?"));
        TypeCompte typeCourant = typeCompteRepository.findByLibelleIgnoreCase("COURANT")
                .orElseThrow(() -> new IllegalStateException("TypeCompte COURANT not found in reference data. Did reference-data.sql run?"));
        ProduitEpargne epargneVue = produitEpargneRepository.findByCodeProduit("EP-VUE").orElse(null);

        Map<String, Compte> comptes = new LinkedHashMap<>();
        Map<String, Utilisateur> utilisateurs = new LinkedHashMap<>();
        int insertedClients = 0;
        int insertedUsers = 0;
        int insertedAccounts = 0;

        for (DemoUserSeed seed : DemoDataSeeds.USERS) {
            boolean clientExists = clientRepository.existsByCodeClient(seed.codeClient());
            Client client = clientRepository.findByCodeClient(seed.codeClient())
                    .orElseGet(() -> {
                        Client createdClient = clientRepository.save(DemoEntityFactory.client(seed, statutActif, agencePrincipale));
                        log.debug("DemoDataLoader: inserted client {}", seed.codeClient());
                        return createdClient;
                    });
            if (!clientExists) {
                insertedClients++;
            }

            boolean userExists = utilisateurRepository.existsByLogin(seed.login());
            Utilisateur utilisateur = utilisateurRepository.findByLogin(seed.login())
                    .orElseGet(() -> {
                        String password = passwordEncoder.encode(DemoDataSeeds.DEMO_PASSWORD);
                        Utilisateur createdUser = utilisateurRepository.save(
                                DemoEntityFactory.utilisateur(seed, client, agencePrincipale, password));
                        log.debug("DemoDataLoader: inserted user {}", seed.login());
                        return createdUser;
                    });
            if (!userExists) {
                insertedUsers++;
            }
            resetDemoUserState(utilisateur);
            assignRoleIfMissing(utilisateur, seed.roleCode());
            utilisateurs.put(seed.login(), utilisateur);

            boolean accountExists = compteRepository.existsByNumCompte(seed.accountNumber());
            Compte compte = compteRepository.findByNumCompte(seed.accountNumber())
                    .orElseGet(() -> {
                        TypeCompte typeCompte = "COURANT".equals(seed.typeCompte()) ? typeCourant : typeEpargne;
                        Compte createdCompte = compteRepository.save(
                                DemoEntityFactory.compte(seed, client, typeCompte, agencePrincipale, epargneVue));
                        log.debug("DemoDataLoader: inserted account {}", seed.accountNumber());
                        return createdCompte;
                    });
            if (!accountExists) {
                insertedAccounts++;
            }
            ensureCompteActif(compte);
            comptes.put(seed.accountNumber(), compte);
        }

        int insertedTransactions = seedTransactions(utilisateurs, comptes);

        log.info(
                "DemoDataLoader: demo data ready. users={}, accounts={}, transactions={}, password={}",
                utilisateurs.size(),
                comptes.size(),
                DemoDataSeeds.TRANSACTIONS.size(),
                DemoDataSeeds.DEMO_PASSWORD
        );
        log.info(
                "DemoDataLoader: inserted if missing -> clients={}, users={}, accounts={}, transactions={}",
                insertedClients,
                insertedUsers,
                insertedAccounts,
                insertedTransactions
        );
    }

    private void assignRoleIfMissing(Utilisateur utilisateur, String roleCode) {
        RoleUtilisateur role = roleUtilisateurRepository.findByCodeRoleUtilisateur(roleCode)
                .orElseThrow(() -> new IllegalStateException("Role " + roleCode + " not found in reference data. Did reference-data.sql run?"));
        boolean alreadyAssigned = utilisateur.getRoles().stream()
                .anyMatch(existingRole -> roleCode.equals(existingRole.getCodeRoleUtilisateur()));
        if (!alreadyAssigned) {
            utilisateur.getRoles().add(role);
            utilisateurRepository.save(utilisateur);
        }
    }

    private void resetDemoUserState(Utilisateur utilisateur) {
        boolean changed = false;

        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            utilisateur.setActif(Boolean.TRUE);
            changed = true;
        }
        if (utilisateur.getCompteExpireLe() != null) {
            utilisateur.setCompteExpireLe(null);
            changed = true;
        }
        if (utilisateur.getCompteVerrouilleJusquAu() != null) {
            utilisateur.setCompteVerrouilleJusquAu(null);
            changed = true;
        }
        if (utilisateur.getNombreEchecsConnexion() == null || utilisateur.getNombreEchecsConnexion() != 0) {
            utilisateur.setNombreEchecsConnexion(0);
            changed = true;
        }
        if (utilisateur.getDernierEchecConnexion() != null) {
            utilisateur.setDernierEchecConnexion(null);
            changed = true;
        }
        if (utilisateur.getOtpChallengeId() != null) {
            utilisateur.setOtpChallengeId(null);
            changed = true;
        }
        if (utilisateur.getOtpHash() != null) {
            utilisateur.setOtpHash(null);
            changed = true;
        }
        if (utilisateur.getOtpExpireLe() != null) {
            utilisateur.setOtpExpireLe(null);
            changed = true;
        }
        if (utilisateur.getOtpTentativesRestantes() == null || utilisateur.getOtpTentativesRestantes() != 0) {
            utilisateur.setOtpTentativesRestantes(0);
            changed = true;
        }
        if (Boolean.TRUE.equals(utilisateur.getSecondFacteurActive())) {
            utilisateur.setSecondFacteurActive(Boolean.FALSE);
            changed = true;
        }
        if (utilisateur.getIdentifiantsExpirentLe() == null || utilisateur.getIdentifiantsExpirentLe().isBefore(LocalDateTime.now().plusDays(7))) {
            utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90));
            changed = true;
        }
        if (utilisateur.getMotDePasseModifieLe() == null) {
            utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
            changed = true;
        }
        if (utilisateur.getPassword() == null || !passwordEncoder.matches(DemoDataSeeds.DEMO_PASSWORD, utilisateur.getPassword())) {
            utilisateur.setPassword(passwordEncoder.encode(DemoDataSeeds.DEMO_PASSWORD));
            utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
            changed = true;
        }

        if (changed) {
            utilisateurRepository.save(utilisateur);
            log.debug("DemoDataLoader: reset demo user state for {}", utilisateur.getLogin());
        }
    }

    private void ensureCompteActif(Compte compte) {
        if (statutCompteRepository.findTopByCompte_IdCompteOrderByDateStatutDesc(compte.getIdCompte()).isPresent()) {
            return;
        }

        statutCompteRepository.save(DemoEntityFactory.statutCompteActif(compte));
    }

    private int seedTransactions(Map<String, Utilisateur> utilisateurs, Map<String, Compte> comptes) {
        Utilisateur initiateur = utilisateurs.getOrDefault("demo.admin", utilisateurs.values().iterator().next());
        int insertedTransactions = 0;

        for (TransactionSeed seed : DemoDataSeeds.TRANSACTIONS) {
            if (transactionRepository.existsByReferenceUnique(seed.referenceUnique())) {
                continue;
            }

            TypeTransaction typeTransaction = typeTransactionRepository.findByCodeTypeTransaction(seed.typeCode())
                    .orElseThrow(() -> new IllegalStateException("TypeTransaction " + seed.typeCode() + " not found in reference data. Did reference-data.sql run?"));
            Compte compteSource = seed.sourceAccountNumber() == null ? null : comptes.get(seed.sourceAccountNumber());
            Compte compteDestination = seed.destinationAccountNumber() == null ? null : comptes.get(seed.destinationAccountNumber());

            Transaction savedTransaction = transactionRepository.save(
                    DemoEntityFactory.transaction(seed, initiateur, typeTransaction, compteSource, compteDestination));
            createLignesEcriture(savedTransaction, seed, compteSource, compteDestination);
            insertedTransactions++;
        }

        return insertedTransactions;
    }

    private void createLignesEcriture(
            Transaction transaction,
            TransactionSeed seed,
            Compte compteSource,
            Compte compteDestination
    ) {
        if ("DEPOT".equals(seed.typeCode())) {
            createLigne(transaction, compteDestination, SensEcriture.CREDIT, seed.montant());
        } else if ("RETRAIT".equals(seed.typeCode())) {
            createLigne(transaction, compteSource, SensEcriture.DEBIT, seed.montant().add(seed.frais()));
        } else if ("VIREMENT".equals(seed.typeCode())) {
            createLigne(transaction, compteSource, SensEcriture.DEBIT, seed.montant().add(seed.frais()));
            createLigne(transaction, compteDestination, SensEcriture.CREDIT, seed.montant());
        }
    }

    private void createLigne(Transaction transaction, Compte compte, SensEcriture sens, BigDecimal montant) {
        if (compte == null) {
            throw new IllegalStateException("Compte manquant pour la transaction " + transaction.getReferenceUnique());
        }

        ligneEcritureRepository.save(DemoEntityFactory.ligne(transaction, compte, sens, montant));
    }

}
