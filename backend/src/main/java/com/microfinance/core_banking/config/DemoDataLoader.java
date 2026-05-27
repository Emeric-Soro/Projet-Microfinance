package com.microfinance.core_banking.config;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.LigneEcriture;
import com.microfinance.core_banking.entity.RoleUtilisateur;
import com.microfinance.core_banking.entity.SensEcriture;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.entity.TypePieceIdentite;
import com.microfinance.core_banking.entity.TypeTransaction;
import com.microfinance.core_banking.entity.Utilisateur;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.StatutCompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.RoleUtilisateurRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.operation.LigneEcritureRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.operation.TypeTransactionRepository;
import com.microfinance.core_banking.repository.parametrage.AgenceRepository;
import com.microfinance.core_banking.repository.parametrage.ProduitEpargneRepository;
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
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);
    private static final String DEMO_PASSWORD = "Demo@12345";
    private static final List<DemoUserSeed> DEMO_USERS = List.of(
            new DemoUserSeed("CLI-DEMO-0001", "Kouadio", "Awa", LocalDate.of(1988, 3, 14), "demo.admin@microfin.local", "+225 07 01 10 10 01", "Cocody, Abidjan", "Commercante", "Commerce de detail", "CNI-CI-2026-0001", "demo.admin", "ADMIN", "EPARGNE", "CI23CB000100000001", new BigDecimal("750000.00")),
            new DemoUserSeed("CLI-20260527-0002", "Koffi", "Yao", LocalDate.of(1991, 8, 7), "yao.koffi@demo.microfin.local", "+225 05 02 10 10 02", "Plateau, Abidjan", "Agent administratif", "Services", "CNI-CI-2026-0002", "yao.koffi", "GUICHETIER", "COURANT", "CI23CB000100000002", new BigDecimal("450000.00")),
            new DemoUserSeed("CLI-20260527-0003", "Konan", "Adjoua", LocalDate.of(1985, 12, 21), "adjoua.konan@demo.microfin.local", "+225 01 03 10 10 03", "Bouake, Air France", "Analyste credit", "Finance", "CNI-CI-2026-0003", "adjoua.konan", "AGENT_CREDIT", "EPARGNE", "CI23CB000100000003", new BigDecimal("620000.00")),
            new DemoUserSeed("CLI-20260527-0004", "N'Guessan", "Serge", LocalDate.of(1982, 5, 9), "serge.nguessan@demo.microfin.local", "+225 07 04 10 10 04", "Marcory, Abidjan", "Chef d'agence", "Management", "CNI-CI-2026-0004", "serge.nguessan", "CHEF_AGENCE", "COURANT", "CI23CB000100000004", new BigDecimal("950000.00")),
            new DemoUserSeed("CLI-20260527-0005", "Bamba", "Fatoumata", LocalDate.of(1994, 2, 18), "fatoumata.bamba@demo.microfin.local", "+225 05 05 10 10 05", "Yopougon, Abidjan", "Restauratrice", "Restauration", "CNI-CI-2026-0005", "fatoumata.bamba", "CLIENT", "EPARGNE", "CI23CB000100000005", new BigDecimal("180000.00")),
            new DemoUserSeed("CLI-20260527-0006", "Ouattara", "Idrissa", LocalDate.of(1990, 10, 30), "idrissa.ouattara@demo.microfin.local", "+225 01 06 10 10 06", "Korhogo, Quartier Commerce", "Transporteur", "Transport", "CNI-CI-2026-0006", "idrissa.ouattara", "CLIENT", "EPARGNE", "CI23CB000100000006", new BigDecimal("125000.00")),
            new DemoUserSeed("CLI-20260527-0007", "Diabate", "Aminata", LocalDate.of(1996, 7, 3), "aminata.diabate@demo.microfin.local", "+225 07 07 10 10 07", "Treichville, Abidjan", "Couturiere", "Artisanat", "CNI-CI-2026-0007", "aminata.diabate", "CLIENT", "COURANT", "CI23CB000100000007", new BigDecimal("340000.00")),
            new DemoUserSeed("CLI-20260527-0008", "Soro", "Mireille", LocalDate.of(1989, 11, 12), "mireille.soro@demo.microfin.local", "+225 05 08 10 10 08", "Daloa, Tazibouo", "Enseignante", "Education", "CNI-CI-2026-0008", "mireille.soro", "CLIENT", "EPARGNE", "CI23CB000100000008", new BigDecimal("275000.00")),
            new DemoUserSeed("CLI-20260527-0009", "Coulibaly", "Yacouba", LocalDate.of(1987, 4, 26), "yacouba.coulibaly@demo.microfin.local", "+225 01 09 10 10 09", "Man, Libreville", "Agriculteur", "Agriculture", "CNI-CI-2026-0009", "yacouba.coulibaly", "CLIENT", "COURANT", "CI23CB000100000009", new BigDecimal("410000.00")),
            new DemoUserSeed("CLI-20260527-0010", "Traore", "Affoue", LocalDate.of(1993, 1, 5), "affoue.traore@demo.microfin.local", "+225 07 10 10 10 10", "San Pedro, Bardot", "Coiffeuse", "Services", "CNI-CI-2026-0010", "affoue.traore", "CLIENT", "EPARGNE", "CI23CB000100000010", new BigDecimal("155000.00")),
            new DemoUserSeed("CLI-20260527-0011", "Gnahore", "Ange", LocalDate.of(1992, 9, 17), "ange.gnahore@demo.microfin.local", "+225 05 11 10 10 11", "Gagnoa, Soleil", "Technicien", "Informatique", "CNI-CI-2026-0011", "ange.gnahore", "CLIENT", "EPARGNE", "CI23CB000100000011", new BigDecimal("210000.00")),
            new DemoUserSeed("CLI-20260527-0012", "Toure", "Nadege", LocalDate.of(1986, 6, 23), "nadege.toure@demo.microfin.local", "+225 01 12 10 10 12", "Abobo, Abidjan", "Grossiste", "Commerce", "CNI-CI-2026-0012", "nadege.toure", "CLIENT", "COURANT", "CI23CB000100000012", new BigDecimal("390000.00"))
    );
    private static final List<TransactionSeed> DEMO_TRANSACTIONS = List.of(
            new TransactionSeed("TRX-DEMO-20260527-0001", "DEPOT", null, "CI23CB000100000005", new BigDecimal("50000.00"), BigDecimal.ZERO, 6),
            new TransactionSeed("TRX-DEMO-20260527-0002", "DEPOT", null, "CI23CB000100000008", new BigDecimal("75000.00"), BigDecimal.ZERO, 5),
            new TransactionSeed("TRX-DEMO-20260527-0003", "RETRAIT", "CI23CB000100000007", null, new BigDecimal("25000.00"), new BigDecimal("250.00"), 4),
            new TransactionSeed("TRX-DEMO-20260527-0004", "VIREMENT", "CI23CB000100000009", "CI23CB000100000010", new BigDecimal("60000.00"), new BigDecimal("500.00"), 3),
            new TransactionSeed("TRX-DEMO-20260527-0005", "DEPOT", null, "CI23CB000100000012", new BigDecimal("120000.00"), BigDecimal.ZERO, 2)
    );

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

        log.info("DemoDataLoader: seeding Ivorian demo data...");

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

        for (DemoUserSeed seed : DEMO_USERS) {
            boolean clientExists = clientRepository.existsByCodeClient(seed.codeClient());
            Client client = clientRepository.findByCodeClient(seed.codeClient())
                    .orElseGet(() -> {
                        Client createdClient = createClient(seed, statutActif, agencePrincipale);
                        log.debug("DemoDataLoader: inserted client {}", seed.codeClient());
                        return createdClient;
                    });
            if (!clientExists) {
                insertedClients++;
            }

            boolean userExists = utilisateurRepository.existsByLogin(seed.login());
            Utilisateur utilisateur = utilisateurRepository.findByLogin(seed.login())
                    .orElseGet(() -> {
                        Utilisateur createdUser = createUser(seed, client, agencePrincipale);
                        log.debug("DemoDataLoader: inserted user {}", seed.login());
                        return createdUser;
                    });
            if (!userExists) {
                insertedUsers++;
            }
            assignRoleIfMissing(utilisateur, seed.roleCode());
            utilisateurs.put(seed.login(), utilisateur);

            boolean accountExists = compteRepository.existsByNumCompte(seed.accountNumber());
            Compte compte = compteRepository.findByNumCompte(seed.accountNumber())
                    .orElseGet(() -> {
                        TypeCompte typeCompte = "COURANT".equals(seed.typeCompte()) ? typeCourant : typeEpargne;
                        Compte createdCompte = createCompte(seed, client, typeCompte, agencePrincipale, epargneVue);
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
                DEMO_TRANSACTIONS.size(),
                DEMO_PASSWORD
        );
        log.info(
                "DemoDataLoader: inserted if missing -> clients={}, users={}, accounts={}, transactions={}",
                insertedClients,
                insertedUsers,
                insertedAccounts,
                insertedTransactions
        );
    }

    private Client createClient(DemoUserSeed seed, StatutClient statutActif, Agence agencePrincipale) {
        Client client = new Client();
        client.setCodeClient(seed.codeClient());
        client.setNom(seed.nom());
        client.setPrenom(seed.prenom());
        client.setDateNaissance(seed.dateNaissance());
        client.setAdresse(seed.adresse());
        client.setTelephone(seed.telephone());
        client.setEmail(seed.email());
        client.setTypePieceIdentite(TypePieceIdentite.CNI);
        client.setNumeroPieceIdentite(seed.numeroPieceIdentite());
        client.setDateExpirationPieceIdentite(LocalDate.now().plusYears(5));
        client.setPhotoIdentiteUrl("demo/kyc/" + seed.login() + "/photo.jpg");
        client.setJustificatifDomicileUrl("demo/kyc/" + seed.login() + "/domicile.pdf");
        client.setJustificatifRevenusUrl("demo/kyc/" + seed.login() + "/revenus.pdf");
        client.setProfession(seed.profession());
        client.setEmployeur(seed.secteurActivite());
        client.setPaysNationalite("Cote d'Ivoire");
        client.setPaysResidence("Cote d'Ivoire");
        client.setPep(false);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setStatutKyc(StatutKycClient.VALIDE);
        client.setDateSoumissionKyc(LocalDate.now().minusDays(30));
        client.setDateValidationKyc(LocalDate.now().minusDays(25));
        client.setCommentaireKyc("Dossier demo valide pour les tests fonctionnels.");
        client.setValidateurKyc("demo.seed");
        client.setDateInscription(LocalDate.now());
        client.setStatutClient(statutActif);
        client.setAgence(agencePrincipale);
        client.setRevenuMensuel(seed.soldeInitial().divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP));
        client.setSecteurActivite(seed.secteurActivite());

        return clientRepository.save(client);
    }

    private Utilisateur createUser(DemoUserSeed seed, Client client, Agence agencePrincipale) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        utilisateur.setLogin(seed.login());
        utilisateur.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
        utilisateur.setActif(true);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setSecondFacteurActive(false);
        utilisateur.setOtpTentativesRestantes(0);
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90));
        utilisateur.setAgence(agencePrincipale);

        return utilisateurRepository.save(utilisateur);
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

    private Compte createCompte(
            DemoUserSeed seed,
            Client client,
            TypeCompte typeCompte,
            Agence agencePrincipale,
            ProduitEpargne epargneVue
    ) {
        Compte compte = new Compte();
        compte.setNumCompte(seed.accountNumber());
        compte.setSolde(seed.soldeInitial());
        compte.setDateOuverture(LocalDate.now().minusDays(20));
        compte.setDevise("XOF");
        compte.setTauxInteret("EPARGNE".equals(seed.typeCompte()) ? new BigDecimal("3.5000") : BigDecimal.ZERO);
        compte.setDecouvertAutorise("COURANT".equals(seed.typeCompte()) ? new BigDecimal("50000.00") : BigDecimal.ZERO);
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setAgence(agencePrincipale);
        compte.setProduitEpargne("EPARGNE".equals(seed.typeCompte()) ? epargneVue : null);

        return compteRepository.save(compte);
    }

    private void ensureCompteActif(Compte compte) {
        if (statutCompteRepository.findTopByCompte_IdCompteOrderByDateStatutDesc(compte.getIdCompte()).isPresent()) {
            return;
        }

        StatutCompte statutCompte = new StatutCompte();
        statutCompte.setCompte(compte);
        statutCompte.setLibelleStatut("ACTIF");
        statutCompte.setDateStatut(LocalDateTime.now().minusDays(20));
        statutCompteRepository.save(statutCompte);
    }

    private int seedTransactions(Map<String, Utilisateur> utilisateurs, Map<String, Compte> comptes) {
        Utilisateur initiateur = utilisateurs.getOrDefault("demo.admin", utilisateurs.values().iterator().next());
        int insertedTransactions = 0;

        for (TransactionSeed seed : DEMO_TRANSACTIONS) {
            if (transactionRepository.existsByReferenceUnique(seed.referenceUnique())) {
                continue;
            }

            TypeTransaction typeTransaction = typeTransactionRepository.findByCodeTypeTransaction(seed.typeCode())
                    .orElseThrow(() -> new IllegalStateException("TypeTransaction " + seed.typeCode() + " not found in reference data. Did reference-data.sql run?"));
            Compte compteSource = seed.sourceAccountNumber() == null ? null : comptes.get(seed.sourceAccountNumber());
            Compte compteDestination = seed.destinationAccountNumber() == null ? null : comptes.get(seed.destinationAccountNumber());

            Transaction transaction = new Transaction();
            transaction.setReferenceUnique(seed.referenceUnique());
            transaction.setDateHeureTransaction(LocalDateTime.now().minusDays(seed.daysAgo()));
            transaction.setMontantGlobal(seed.montant());
            transaction.setFrais(seed.frais());
            transaction.setStatutOperation(StatutOperation.EXECUTEE);
            transaction.setValidationSuperviseurRequise(false);
            transaction.setDateExecution(transaction.getDateHeureTransaction().plusMinutes(2));
            transaction.setUtilisateur(initiateur);
            transaction.setTypeTransaction(typeTransaction);
            transaction.setCompteSource(compteSource);
            transaction.setCompteDestination(compteDestination);

            Transaction savedTransaction = transactionRepository.save(transaction);
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

        LigneEcriture ligne = new LigneEcriture();
        ligne.setTransaction(transaction);
        ligne.setCompte(compte);
        ligne.setSens(sens);
        ligne.setMontant(montant);
        ligneEcritureRepository.save(ligne);
    }

    private record DemoUserSeed(
            String codeClient,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String email,
            String telephone,
            String adresse,
            String profession,
            String secteurActivite,
            String numeroPieceIdentite,
            String login,
            String roleCode,
            String typeCompte,
            String accountNumber,
            BigDecimal soldeInitial
    ) {
    }

    private record TransactionSeed(
            String referenceUnique,
            String typeCode,
            String sourceAccountNumber,
            String destinationAccountNumber,
            BigDecimal montant,
            BigDecimal frais,
            int daysAgo
    ) {
    }
}
