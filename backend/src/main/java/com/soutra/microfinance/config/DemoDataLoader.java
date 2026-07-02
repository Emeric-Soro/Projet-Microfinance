package com.soutra.microfinance.config;

import com.soutra.microfinance.config.DemoDataSeeds.DemoClientSeed;
import com.soutra.microfinance.config.DemoDataSeeds.DemoCreditSeed;
import com.soutra.microfinance.config.DemoDataSeeds.DemoUserSeed;
import com.soutra.microfinance.config.DemoDataSeeds.TransactionSeed;
import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.entity.Beneficiaire;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.entity.DocumentClient;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.Garantie;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.entity.RoleUtilisateur;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutCredit;
import com.soutra.microfinance.entity.StatutEnvoi;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeCanal;
import com.soutra.microfinance.entity.TypeCompte;
import com.soutra.microfinance.entity.TypeGarantie;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;
import com.soutra.microfinance.entity.Caisse;
import com.soutra.microfinance.repository.client.BeneficiaireRepository;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.DocumentClientRepository;
import com.soutra.microfinance.repository.client.RoleUtilisateurRepository;
import com.soutra.microfinance.repository.client.StatutClientRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.repository.communication.NotificationRepository;
import com.soutra.microfinance.repository.communication.StatutEnvoiRepository;
import com.soutra.microfinance.repository.communication.TypeCanalRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.compte.StatutCompteRepository;
import com.soutra.microfinance.repository.compte.TypeCompteRepository;
import com.soutra.microfinance.repository.credit.CreditRepository;
import com.soutra.microfinance.repository.credit.DemandeCreditRepository;
import com.soutra.microfinance.repository.credit.EcheanceRepository;
import com.soutra.microfinance.repository.credit.GarantieRepository;
import com.soutra.microfinance.repository.credit.ProduitCreditRepository;
import com.soutra.microfinance.repository.credit.StatutCreditRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DemoDataLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);
    private static final DateTimeFormatter TXN_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    // --- Repositories existants ---
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
    private final CaisseRepository caisseRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedOnStartup;

    // --- Nouveaux repositories ---
    private final DemandeCreditRepository demandeCreditRepository;
    private final CreditRepository creditRepository;
    private final EcheanceRepository echeanceRepository;
    private final GarantieRepository garantieRepository;
    private final ProduitCreditRepository produitCreditRepository;
    private final StatutCreditRepository statutCreditRepository;
    private final NotificationRepository notificationRepository;
    private final TypeCanalRepository typeCanalRepository;
    private final StatutEnvoiRepository statutEnvoiRepository;
    private final BeneficiaireRepository beneficiaireRepository;
    private final DocumentClientRepository documentClientRepository;

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
            CaisseRepository caisseRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo-data.seed-on-startup:true}") boolean seedOnStartup,
            DemandeCreditRepository demandeCreditRepository,
            CreditRepository creditRepository,
            EcheanceRepository echeanceRepository,
            GarantieRepository garantieRepository,
            ProduitCreditRepository produitCreditRepository,
            StatutCreditRepository statutCreditRepository,
            NotificationRepository notificationRepository,
            TypeCanalRepository typeCanalRepository,
            StatutEnvoiRepository statutEnvoiRepository,
            BeneficiaireRepository beneficiaireRepository,
            DocumentClientRepository documentClientRepository
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
        this.caisseRepository = caisseRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedOnStartup = seedOnStartup;
        this.demandeCreditRepository = demandeCreditRepository;
        this.creditRepository = creditRepository;
        this.echeanceRepository = echeanceRepository;
        this.garantieRepository = garantieRepository;
        this.produitCreditRepository = produitCreditRepository;
        this.statutCreditRepository = statutCreditRepository;
        this.notificationRepository = notificationRepository;
        this.typeCanalRepository = typeCanalRepository;
        this.statutEnvoiRepository = statutEnvoiRepository;
        this.beneficiaireRepository = beneficiaireRepository;
        this.documentClientRepository = documentClientRepository;
    }

    // =========================================================================
    // POINT D'ENTRÉE PRINCIPAL
    // =========================================================================

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedOnStartup) {
            log.info("DemoDataLoader: demo data seeding disabled by configuration.");
            return;
        }

        log.info("DemoDataLoader: seeding SOUTRA demo data (target: 150 clients, 300 comptes, 600 transactions)...");

        // ── Données de référence ──────────────────────────────────────────────
        StatutClient statutActif = statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")
                .orElseThrow(() -> new IllegalStateException("StatutClient ACTIF not found. Did reference-data.sql run?"));
        StatutClient statutNouveau = statutClientRepository.findByLibelleStatutIgnoreCase("NOUVEAU")
                .orElse(statutActif);
        StatutClient statutBloque = statutClientRepository.findByLibelleStatutIgnoreCase("BLOQUE")
                .orElse(statutActif);

        Agence agencePrincipale = agenceRepository.findByCodeAgence("AG-001")
                .orElseThrow(() -> new IllegalStateException("Agence AG-001 not found. Did reference-data.sql run?"));

        TypeCompte typeEpargne = typeCompteRepository.findByLibelleIgnoreCase("EPARGNE")
                .orElseThrow(() -> new IllegalStateException("TypeCompte EPARGNE not found."));
        TypeCompte typeCourant = typeCompteRepository.findByLibelleIgnoreCase("COURANT")
                .orElseThrow(() -> new IllegalStateException("TypeCompte COURANT not found."));
        TypeCompte typeDAT = typeCompteRepository.findByLibelleIgnoreCase("DAT").orElse(null);

        ProduitEpargne epargneVue = produitEpargneRepository.findByCodeProduit("EP-VUE").orElse(null);
        ProduitEpargne depotTerme6M = produitEpargneRepository.findByCodeProduit("EP-DAT-6M").orElse(epargneVue);
        ProduitEpargne depotTerme12M = produitEpargneRepository.findByCodeProduit("EP-DAT-12M").orElse(epargneVue);

        // ── PHASE 1 : Utilisateurs staff + clients liés (existants) ──────────
        Map<String, Compte> comptes = new LinkedHashMap<>();
        Map<String, Utilisateur> utilisateurs = new LinkedHashMap<>();
        int insertedClients = 0, insertedUsers = 0, insertedAccounts = 0;

        for (DemoUserSeed seed : DemoDataSeeds.USERS) {
            boolean clientExists = clientRepository.existsByCodeClient(seed.codeClient());
            Client client = clientRepository.findByCodeClient(seed.codeClient())
                    .orElseGet(() -> {
                        Client c = clientRepository.save(DemoEntityFactory.client(seed, statutActif, agencePrincipale));
                        log.debug("DemoDataLoader: inserted staff client {}", seed.codeClient());
                        return c;
                    });
            if (!clientExists) insertedClients++;

            boolean userExists = utilisateurRepository.existsByLogin(seed.login());
            Utilisateur utilisateur = utilisateurRepository.findByLogin(seed.login())
                    .orElseGet(() -> {
                        String pwd = passwordEncoder.encode(DemoDataSeeds.DEMO_PASSWORD);
                        Utilisateur u = utilisateurRepository.save(
                                DemoEntityFactory.utilisateur(seed, client, agencePrincipale, pwd));
                        log.debug("DemoDataLoader: inserted user {}", seed.login());
                        return u;
                    });
            if (!userExists) insertedUsers++;

            resetDemoUserState(utilisateur);
            assignRoleIfMissing(utilisateur, seed.roleCode());
            utilisateurs.put(seed.login(), utilisateur);

            boolean accountExists = compteRepository.existsByNumCompte(seed.accountNumber());
            Compte compte = compteRepository.findByNumCompte(seed.accountNumber())
                    .orElseGet(() -> {
                        TypeCompte tc = "COURANT".equals(seed.typeCompte()) ? typeCourant : typeEpargne;
                        Compte c = compteRepository.save(
                                DemoEntityFactory.compte(seed, client, tc, agencePrincipale, epargneVue));
                        log.debug("DemoDataLoader: inserted account {}", seed.accountNumber());
                        return c;
                    });
            if (!accountExists) insertedAccounts++;
            ensureCompteActif(compte);
            comptes.put(seed.accountNumber(), compte);
        }

        // Transactions existantes
        int insertedTxn = seedTransactions(utilisateurs, comptes);
        seedCaisses(utilisateurs);

        // ── PHASE 2 : 133 clients purs ────────────────────────────────────────
        Map<String, Client> allClients = new LinkedHashMap<>();
        // Enregistrer les clients staff dans la map globale
        for (DemoUserSeed seed : DemoDataSeeds.USERS) {
            clientRepository.findByCodeClient(seed.codeClient()).ifPresent(c -> allClients.put(seed.codeClient(), c));
        }

        int clientsPursInseres = seedClientsOnly(
                DemoDataSeeds.CLIENTS, allClients,
                statutActif, statutNouveau, statutBloque, agencePrincipale);

        // ── PHASE 3 : Comptes supplémentaires (300 total) ─────────────────────
        int comptesInseres = seedAccountsSupplementaires(
                allClients, comptes,
                typeEpargne, typeCourant, typeDAT,
                epargneVue, depotTerme6M, depotTerme12M, agencePrincipale);

        // ── PHASE 4 : Crédits (80 demandes, 50 actifs) ────────────────────────
        Utilisateur agentCredit = utilisateurs.getOrDefault("adjoua.konan",
                utilisateurs.values().iterator().next());
        int[] creditStats = seedCredits(allClients, comptes, agentCredit);

        // ── PHASE 5 : Transactions massives historiques (600) ─────────────────
        int txnMassives = seedTransactionsMassives(allClients, comptes, utilisateurs);

        // ── PHASE 6 : Notifications (200) ─────────────────────────────────────
        int notifs = seedNotifications(allClients);

        // ── PHASE 7 : Bénéficiaires (100) ─────────────────────────────────────
        int beneficiaires = seedBeneficiaires(allClients, comptes);

        // ── PHASE 8 : Documents client (150) ──────────────────────────────────
        int docs = seedDocuments(allClients, utilisateurs);

        log.info("DemoDataLoader: COMPLETED ─ " +
                        "staff_clients={} staff_users={} staff_accounts={} staff_txn={} | " +
                        "clients_purs={} comptes_suppl={} | " +
                        "demandes={} credits_actifs={} echeances={} garanties={} | " +
                        "txn_massives={} | notifs={} beneficiaires={} docs={}",
                insertedClients, insertedUsers, insertedAccounts, insertedTxn,
                clientsPursInseres, comptesInseres,
                creditStats[0], creditStats[1], creditStats[2], creditStats[3],
                txnMassives, notifs, beneficiaires, docs);
        log.info("DemoDataLoader: Mot de passe démo = {}", DemoDataSeeds.DEMO_PASSWORD);
    }

    // =========================================================================
    // PHASE 2 : CLIENTS PURS
    // =========================================================================

    private int seedClientsOnly(List<DemoClientSeed> seeds,
                                Map<String, Client> allClientsOut,
                                StatutClient actif, StatutClient nouveau, StatutClient bloque,
                                Agence agence) {
        int count = 0;
        for (DemoClientSeed seed : seeds) {
            if (clientRepository.existsByCodeClient(seed.codeClient())) {
                clientRepository.findByCodeClient(seed.codeClient()).ifPresent(
                        c -> allClientsOut.put(seed.codeClient(), c));
                continue;
            }
            StatutClient statut = switch (seed.statutClientCode()) {
                case "NOUVEAU" -> nouveau;
                case "BLOQUE" -> bloque;
                default -> actif;
            };
            Client client = clientRepository.save(DemoEntityFactory.clientPur(seed, statut, agence));
            allClientsOut.put(seed.codeClient(), client);
            count++;
        }
        log.info("DemoDataLoader: seedClientsOnly → {} nouveaux clients créés", count);
        return count;
    }

    // =========================================================================
    // PHASE 3 : COMPTES SUPPLÉMENTAIRES
    // Objectif : 300 comptes total
    // Règle PRD : 20% 1 compte, 60% 2 comptes, 20% 3 comptes
    // =========================================================================

    private int seedAccountsSupplementaires(
            Map<String, Client> allClients,
            Map<String, Compte> comptesOut,
            TypeCompte typeEpargne, TypeCompte typeCourant, TypeCompte typeDAT,
            ProduitEpargne epargneVue, ProduitEpargne dat6M, ProduitEpargne dat12M,
            Agence agence) {
        int count = 0;
        int clientIndex = 0;

        for (Map.Entry<String, Client> entry : allClients.entrySet()) {
            Client client = entry.getValue();
            String code = entry.getKey();
            clientIndex++;

            // Compte épargne principal (si pas déjà créé via DemoUserSeed)
            String numEpargne = numCompte(clientIndex, 1);
            if (!compteRepository.existsByNumCompte(numEpargne) && client.getComptes().isEmpty()) {
                BigDecimal soldeEp = soldeAleatoire(clientIndex, 25000, 500000);
                Compte ep = compteRepository.save(DemoEntityFactory.compteSupplementaire(
                        client, typeEpargne, numEpargne, soldeEp,
                        new BigDecimal("3.5000"), BigDecimal.ZERO,
                        agence, epargneVue, 90 + (clientIndex % 200)));
                ensureCompteActif(ep);
                comptesOut.put(numEpargne, ep);
                count++;
            }

            // 80% des clients ont un compte courant
            if (clientIndex % 5 != 0) {
                String numCourant = numCompte(clientIndex, 2);
                if (!compteRepository.existsByNumCompte(numCourant)) {
                    BigDecimal soldeCo = soldeAleatoire(clientIndex * 3, 10000, 800000);
                    Compte co = compteRepository.save(DemoEntityFactory.compteSupplementaire(
                            client, typeCourant, numCourant, soldeCo,
                            BigDecimal.ZERO, new BigDecimal("100000.00"),
                            agence, null, 60 + (clientIndex % 180)));
                    ensureCompteActif(co);
                    comptesOut.put(numCourant, co);
                    count++;
                }
            }

            // 20% des clients ont un DAT
            if (typeDAT != null && clientIndex % 5 == 1) {
                String numDAT = numCompte(clientIndex, 3);
                if (!compteRepository.existsByNumCompte(numDAT)) {
                    BigDecimal soldeDAT = soldeAleatoire(clientIndex * 7, 500000, 5000000);
                    ProduitEpargne prodDAT = (clientIndex % 2 == 0) ? dat6M : dat12M;
                    BigDecimal tauxDAT = (clientIndex % 2 == 0) ? new BigDecimal("5.5000") : new BigDecimal("7.0000");
                    Compte dat = compteRepository.save(DemoEntityFactory.compteSupplementaire(
                            client, typeDAT, numDAT, soldeDAT,
                            tauxDAT, BigDecimal.ZERO,
                            agence, prodDAT, 30 + (clientIndex % 120)));
                    ensureCompteActif(dat);
                    comptesOut.put(numDAT, dat);
                    count++;
                }
            }
        }
        log.info("DemoDataLoader: seedAccountsSupplementaires → {} comptes créés", count);
        return count;
    }

    // =========================================================================
    // PHASE 4 : CRÉDITS (80 demandes + 50 actifs + échéanciers + garanties)
    // =========================================================================

    private int[] seedCredits(Map<String, Client> allClients,
                              Map<String, Compte> comptes,
                              Utilisateur agentCredit) {
        int demandes = 0, credits = 0, echeances = 0, garanties = 0;

        // Charger les produits crédit
        ProduitCredit prodComm = produitCreditRepository.findByCodeProduit("MC-COMMERCE").orElse(null);
        ProduitCredit prodAgri = produitCreditRepository.findByCodeProduit("MC-AGRICULTURE").orElse(null);
        ProduitCredit prodSal = produitCreditRepository.findByCodeProduit("PRET-SALARIE").orElse(null);

        if (prodComm == null && prodAgri == null && prodSal == null) {
            log.warn("DemoDataLoader: Aucun produit crédit trouvé. Les crédits ne seront pas créés.");
            return new int[]{0, 0, 0, 0};
        }

        // Statuts crédit
        StatutCredit scEnCours = statutCreditRepository.findByCodeStatut("EN_COURS").orElse(null);
        StatutCredit scEnRetard = statutCreditRepository.findByCodeStatut("EN_RETARD").orElse(null);
        StatutCredit scSolde = statutCreditRepository.findByCodeStatut("SOLDE").orElse(null);
        StatutCredit scSouffrance = statutCreditRepository.findByCodeStatut("SOUFFRANCE").orElse(null);

        if (scEnCours == null) {
            log.warn("DemoDataLoader: StatutCredit EN_COURS non trouvé. Les crédits ne seront pas créés.");
            return new int[]{0, 0, 0, 0};
        }

        for (DemoCreditSeed seed : DemoDataSeeds.CREDIT_SEEDS) {
            Client client = allClients.get(seed.codeClient());
            if (client == null) {
                log.warn("DemoDataLoader: client {} non trouvé pour crédit, ignoré.", seed.codeClient());
                continue;
            }

            ProduitCredit produit = switch (seed.produitCode()) {
                case "MC-AGRICULTURE" -> prodAgri != null ? prodAgri : prodComm;
                case "PRET-SALARIE" -> prodSal != null ? prodSal : prodComm;
                default -> prodComm;
            };
            if (produit == null) continue;

            // Taux selon produit
            BigDecimal taux = switch (seed.produitCode()) {
                case "MC-AGRICULTURE" -> new BigDecimal("15.0000");
                case "PRET-SALARIE" -> new BigDecimal("12.0000");
                default -> new BigDecimal("18.0000");
            };

            // Vérifier si la demande existe déjà
            String datePart = seed.dateDemande().replace("-", "");
            String seqPart = seed.codeClient().replaceAll("[^0-9]", "");
            if (seqPart.length() > 4) seqPart = seqPart.substring(seqPart.length() - 4);
            String refDemande = "DEM-" + datePart + "-" + seqPart;

            if (demandeCreditRepository.findByReferenceDemande(refDemande).isPresent()) continue;

            // Créer la demande
            DemandeCredit demande = demandeCreditRepository.save(
                    DemoEntityFactory.demandeCredit(seed, client, produit, agentCredit));
            demandes++;

            // Créer le crédit si décaissé
            if (seed.statutCredit() != null) {
                StatutCredit sc = switch (seed.statutCredit()) {
                    case "EN_RETARD" -> scEnRetard != null ? scEnRetard : scEnCours;
                    case "SOLDE" -> scSolde != null ? scSolde : scEnCours;
                    case "SOUFFRANCE" -> scSouffrance != null ? scSouffrance : scEnCours;
                    default -> scEnCours;
                };

                // Trouver un compte courant ou épargne pour le décaissement
                Compte compteDecaissement = trouverCompteClient(client, comptes);
                if (compteDecaissement == null) continue;

                LocalDate dateDecaissement = LocalDate.parse(seed.dateDemande()).plusDays(14);

                // Calcul préventif de la référence pour le garde-fou idempotent
                String clientSuffix = seed.codeClient().replaceAll("[^0-9A-Za-z]", "");
                if (clientSuffix.length() > 6) clientSuffix = clientSuffix.substring(clientSuffix.length() - 6);
                String refCredit = "CRD-" + dateDecaissement.toString().replace("-", "") + "-" + clientSuffix;
                if (creditRepository.existsByReferenceCredit(refCredit)) continue;

                Credit credit = creditRepository.save(DemoEntityFactory.credit(
                        demande, sc, compteDecaissement, taux, seed.dureeMois(), dateDecaissement, seed.codeClient()));
                credits++;

                // Générer les échéances
                int nbEch = genererEcheances(credit, seed, dateDecaissement);
                echeances += nbEch;

                // Créer la garantie
                Garantie garantie = garantieRepository.save(genererGarantie(credit, seed));
                garanties++;

                log.debug("DemoDataLoader: crédit {} créé ({} échéances)", credit.getReferenceCredit(), nbEch);
            }
        }

        log.info("DemoDataLoader: seedCredits → demandes={} credits={} echeances={} garanties={}",
                demandes, credits, echeances, garanties);
        return new int[]{demandes, credits, echeances, garanties};
    }

    private Compte trouverCompteClient(Client client, Map<String, Compte> comptesMap) {
        return comptesMap.values().stream()
                .filter(c -> c.getClient() != null && c.getClient().getIdClient().equals(client.getIdClient()))
                .findFirst()
                .orElse(null);
    }

    private int genererEcheances(Credit credit, DemoCreditSeed seed, LocalDate dateDecaissement) {
        int dureeMois = seed.dureeMois();
        BigDecimal montant = credit.getMontantAccorde();
        BigDecimal tauxMensuel = credit.getTauxInteretAnnuel()
                .divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);

        // Calcul annuité constante (méthode dégressive)
        BigDecimal annuite = calculerAnnuite(montant, tauxMensuel, dureeMois);
        BigDecimal capitalRestant = montant;
        int created = 0;
        boolean estSolde = "SOLDE".equals(seed.statutCredit());
        boolean estEnRetard = "EN_RETARD".equals(seed.statutCredit()) || "SOUFFRANCE".equals(seed.statutCredit());

        for (int i = 1; i <= dureeMois; i++) {
            LocalDate dateEch = dateDecaissement.plusMonths(i);
            BigDecimal interet = capitalRestant.multiply(tauxMensuel).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = annuite.subtract(interet).setScale(2, RoundingMode.HALF_UP);
            if (capital.compareTo(capitalRestant) > 0) capital = capitalRestant;
            capitalRestant = capitalRestant.subtract(capital).setScale(2, RoundingMode.HALF_UP);

            // Déterminer si payée
            boolean estPayee;
            LocalDate datePaiement = null;
            BigDecimal penalite = BigDecimal.ZERO;

            if (estSolde) {
                estPayee = true;
                datePaiement = dateEch;
            } else {
                // Les 3 dernières échéances sont impayées si EN_RETARD
                int echeancesRestantes = dureeMois - i;
                if (dateEch.isAfter(LocalDate.now())) {
                    // Échéance future → non payée
                    estPayee = false;
                } else if (estEnRetard && echeancesRestantes < 3) {
                    // Dernières échéances → impayées (retard)
                    estPayee = false;
                } else {
                    estPayee = true;
                    // Simuler retard de paiement pour ~15%
                    if (i % 7 == 0) {
                        datePaiement = dateEch.plusDays(5 + (i % 10));
                        penalite = annuite.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        datePaiement = dateEch;
                    }
                }
            }

            Echeance ech = DemoEntityFactory.echeance(
                    credit, i, dateEch, capital, interet, estPayee, datePaiement, penalite);
            echeanceRepository.save(ech);
            created++;
        }
        return created;
    }

    private BigDecimal calculerAnnuite(BigDecimal montant, BigDecimal tauxMensuel, int dureeMois) {
        // Formule : M * t / (1 - (1+t)^-n)
        if (tauxMensuel.compareTo(BigDecimal.ZERO) == 0) {
            return montant.divide(new BigDecimal(dureeMois), 2, RoundingMode.HALF_UP);
        }
        double t = tauxMensuel.doubleValue();
        double n = dureeMois;
        double m = montant.doubleValue();
        double annuite = m * t / (1 - Math.pow(1 + t, -n));
        return BigDecimal.valueOf(annuite).setScale(2, RoundingMode.HALF_UP);
    }

    private Garantie genererGarantie(Credit credit, DemoCreditSeed seed) {
        int h = Math.abs(seed.codeClient().hashCode() % 4);
        return switch (h) {
            case 0 -> DemoEntityFactory.garantie(credit, TypeGarantie.GAGE,
                    "Moto Honda 125cc immatriculée",
                    credit.getMontantAccorde().multiply(new BigDecimal("0.6")).setScale(0, RoundingMode.HALF_UP));
            case 1 -> DemoEntityFactory.garantie(credit, TypeGarantie.HYPOTHEQUE,
                    "Terrain titré de 200m² à " + (seed.codeClient().hashCode() % 2 == 0 ? "Abidjan" : "Bouaké"),
                    credit.getMontantAccorde().multiply(new BigDecimal("3.0")).setScale(0, RoundingMode.HALF_UP));
            case 2 -> DemoEntityFactory.garantie(credit, TypeGarantie.NANTISSEMENT,
                    "Équipement professionnel " + seed.objetCredit().substring(0, Math.min(30, seed.objetCredit().length())),
                    credit.getMontantAccorde().multiply(new BigDecimal("0.8")).setScale(0, RoundingMode.HALF_UP));
            default -> DemoEntityFactory.garantie(credit, TypeGarantie.CAUTION_SOLIDAIRE,
                    "Caution solidaire d'un tiers",
                    BigDecimal.ZERO);
        };
    }

    // =========================================================================
    // PHASE 5 : TRANSACTIONS MASSIVES HISTORIQUES (600 opérations)
    // =========================================================================

    private int seedTransactionsMassives(Map<String, Client> allClients,
                                         Map<String, Compte> comptesMap,
                                         Map<String, Utilisateur> utilisateurs) {
        int count = 0;
        List<Compte> listeComptes = new ArrayList<>(comptesMap.values());
        if (listeComptes.isEmpty()) return 0;

        TypeTransaction typeDepot = typeTransactionRepository.findByCodeTypeTransaction("DEPOT").orElse(null);
        TypeTransaction typeRetrait = typeTransactionRepository.findByCodeTypeTransaction("RETRAIT").orElse(null);
        TypeTransaction typeVirement = typeTransactionRepository.findByCodeTypeTransaction("VIREMENT").orElse(null);

        if (typeDepot == null || typeRetrait == null || typeVirement == null) {
            log.warn("DemoDataLoader: Types de transaction manquants, transactions massives ignorées.");
            return 0;
        }

        List<Utilisateur> guichetiers = utilisateurs.values().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "GUICHETIER".equals(r.getCodeRoleUtilisateur())))
                .toList();
        if (guichetiers.isEmpty()) {
            guichetiers = new ArrayList<>(utilisateurs.values());
        }

        // Distribution mensuelle selon le PRD (Jan 2025 → Juin 2026)
        int[][] distribution = {
            {2025, 1, 60}, {2025, 2, 55}, {2025, 3, 65}, {2025, 4, 50},
            {2025, 5, 60}, {2025, 6, 55}, {2025, 7, 45}, {2025, 8, 40},
            {2025, 9, 35}, {2025, 10, 30}, {2025, 11, 30}, {2025, 12, 35},
            {2026, 1, 10}, {2026, 2, 5}, {2026, 3, 5}, {2026, 4, 0},
            {2026, 5, 0}, {2026, 6, 5}
        };

        // Heures d'opération réalistes
        int[][] heures = {{8, 10}, {10, 12}, {12, 14}, {14, 16}, {16, 18}};
        int[] pctHeures = {25, 20, 10, 25, 20};

        int txnGlobal = 0;

        for (int[] mois : distribution) {
            int annee = mois[0], m = mois[1], cible = mois[2];
            if (cible == 0) continue;

            int jMax = LocalDate.of(annee, m, 1).lengthOfMonth();

            for (int i = 0; i < cible; i++) {
                // Jour aléatoire dans le mois
                int jour = 1 + (i * 31 / Math.max(cible, 1)) % jMax;
                if (jour > jMax) jour = jMax;

                // Heure selon distribution
                int[] tranche = heures[rnd(i, pctHeures)];
                int heure = tranche[0] + (i % (tranche[1] - tranche[0]));
                int minute = (i * 17) % 60;

                LocalDateTime dt = LocalDateTime.of(annee, m, jour, heure, minute, 0);
                String refBase = dt.format(TXN_DATE);
                String ref = "TXN-" + refBase + "-M" + String.format("%06d", ++txnGlobal);

                if (transactionRepository.existsByReferenceUnique(ref)) continue;

                // Type de transaction (distribution PRD)
                String typeCode = typeTxn(i);
                Compte compteSource = listeComptes.get((i * 7 + m) % listeComptes.size());
                Compte compteDest = null;
                BigDecimal montant;
                BigDecimal frais = BigDecimal.ZERO;
                TypeTransaction typeTxObj;
                StatutOperation statut = (i % 10 < 9) ? StatutOperation.EXECUTEE :
                        (i % 10 == 9 ? StatutOperation.EN_ATTENTE : StatutOperation.REJETEE);

                switch (typeCode) {
                    case "DEPOT" -> {
                        montant = soldeAleatoire(i + m * 100, 5000, 500000);
                        typeTxObj = typeDepot;
                        compteSource = null;
                        compteDest = listeComptes.get((i * 3 + m * 5) % listeComptes.size());
                    }
                    case "RETRAIT" -> {
                        montant = soldeAleatoire(i + m * 200, 1000, 300000);
                        typeTxObj = typeRetrait;
                        compteDest = null;
                        frais = montant.compareTo(new BigDecimal("500000")) > 0
                                ? new BigDecimal("500") : BigDecimal.ZERO;
                    }
                    default -> { // VIREMENT
                        montant = soldeAleatoire(i + m * 300, 5000, 1000000);
                        typeTxObj = typeVirement;
                        int destIdx = ((i * 11 + m * 7) % listeComptes.size());
                        compteDest = listeComptes.get(destIdx);
                        if (compteDest.equals(compteSource)) {
                            destIdx = (destIdx + 1) % listeComptes.size();
                            compteDest = listeComptes.get(destIdx);
                        }
                        frais = new BigDecimal("500");
                    }
                }

                Utilisateur initiateur = guichetiers.get(i % guichetiers.size());
                boolean valReq = montant.compareTo(new BigDecimal("500000")) >= 0;

                try {
                    Transaction t = transactionRepository.save(DemoEntityFactory.transactionHistorique(
                            ref, dt, montant, frais, statut, valReq,
                            initiateur, typeTxObj, compteSource, compteDest));
                    createLignesEcritureTxn(t, typeCode, compteSource, compteDest, montant, frais, statut);
                    count++;
                } catch (Exception e) {
                    log.debug("DemoDataLoader: erreur création transaction {}: {}", ref, e.getMessage());
                }
            }
        }

        log.info("DemoDataLoader: seedTransactionsMassives → {} transactions créées", count);
        return count;
    }

    private String typeTxn(int i) {
        // Distribution : DEPOT 30%, RETRAIT 25%, VIREMENT 20%, autres 25%
        int r = i % 100;
        if (r < 30) return "DEPOT";
        if (r < 55) return "RETRAIT";
        return "VIREMENT";
    }

    private int rnd(int seed, int[] pcts) {
        int r = Math.abs(seed % 100);
        int cum = 0;
        for (int j = 0; j < pcts.length; j++) {
            cum += pcts[j];
            if (r < cum) return j;
        }
        return pcts.length - 1;
    }

    private void createLignesEcritureTxn(Transaction t, String typeCode,
                                          Compte source, Compte dest,
                                          BigDecimal montant, BigDecimal frais,
                                          StatutOperation statut) {
        if (statut != StatutOperation.EXECUTEE) return;
        try {
            if ("DEPOT".equals(typeCode) && dest != null) {
                createLigne(t, dest, SensEcriture.CREDIT, montant);
            } else if ("RETRAIT".equals(typeCode) && source != null) {
                createLigne(t, source, SensEcriture.DEBIT, montant.add(frais));
            } else if ("VIREMENT".equals(typeCode)) {
                if (source != null) createLigne(t, source, SensEcriture.DEBIT, montant.add(frais));
                if (dest != null) createLigne(t, dest, SensEcriture.CREDIT, montant);
            }
        } catch (Exception e) {
            log.debug("DemoDataLoader: erreur lignes écriture {}: {}", t.getReferenceUnique(), e.getMessage());
        }
    }

    // =========================================================================
    // PHASE 6 : NOTIFICATIONS (200)
    // =========================================================================

    private int seedNotifications(Map<String, Client> allClients) {
        TypeCanal sms = typeCanalRepository.findByCodeCanal("SMS").orElse(null);
        TypeCanal email = typeCanalRepository.findByCodeCanal("EMAIL").orElse(null);
        StatutEnvoi envoye = statutEnvoiRepository.findByCodeStatutEnvoi("ENVOYE").orElse(null);
        StatutEnvoi enAttente = statutEnvoiRepository.findByCodeStatutEnvoi("EN_ATTENTE").orElse(null);
        StatutEnvoi echec = statutEnvoiRepository.findByCodeStatutEnvoi("ECHEC").orElse(null);

        if (sms == null || envoye == null) {
            log.warn("DemoDataLoader: Canal SMS ou statut ENVOYE non trouvé. Notifications ignorées.");
            return 0;
        }

        TypeCanal emailCanal = email != null ? email : sms;
        StatutEnvoi statutAttente = enAttente != null ? enAttente : envoye;
        StatutEnvoi statutEchec = echec != null ? echec : envoye;

        List<Client> clients = new ArrayList<>(allClients.values());
        if (clients.isEmpty()) return 0;

        // Templates de messages
        String[][] templates = {
            {"DEPOT", "Votre compte a été crédité de %s FCFA. Solde disponible : %s FCFA."},
            {"RETRAIT", "Retrait de %s FCFA effectué avec succès sur votre compte."},
            {"SOLDE_BAS", "Alerte : Votre solde est inférieur au seuil minimum (5 000 FCFA)."},
            {"ECHEANCE", "Rappel : Une échéance de %s FCFA est due le %s. Veuillez procéder au remboursement."},
            {"CREDIT", "Félicitations ! Votre crédit de %s FCFA a été approuvé et décaissé sur votre compte."},
            {"SUSPECT", "Transaction suspecte détectée sur votre compte. Contactez votre agence immédiatement."},
            {"INFO", "SOUTRA Microfinance vous informe : Nos agences seront fermées le 07 août 2025 (fête nationale)."},
        };

        int count = 0;
        int clientIdx = 0;

        for (int i = 0; i < 200 && clientIdx < clients.size() * 3; i++, clientIdx++) {
            Client client = clients.get(clientIdx % clients.size());
            int tmplIdx = i % templates.length;
            String type = templates[tmplIdx][0];
            String msgTemplate = templates[tmplIdx][1];

            // Formater le message
            String msg = switch (type) {
                case "DEPOT" -> String.format(msgTemplate, formatMontant(25000 + (i * 5000) % 500000),
                        formatMontant(50000 + (i * 3000) % 800000));
                case "RETRAIT" -> String.format(msgTemplate, formatMontant(10000 + (i * 3000) % 200000));
                case "ECHEANCE" -> String.format(msgTemplate, formatMontant(80000 + (i * 1000) % 200000),
                        LocalDate.now().plusDays(3 + i % 10).toString());
                case "CREDIT" -> String.format(msgTemplate, formatMontant(500000 + (i * 50000) % 3000000));
                default -> msgTemplate;
            };

            // Canal et statut
            TypeCanal canal = (i % 4 != 0) ? sms : emailCanal;
            StatutEnvoi statut = (i % 10 < 8) ? envoye : (i % 10 < 9 ? statutAttente : statutEchec);
            LocalDate dateEnvoi = (statut == envoye) ? LocalDate.now().minusDays(i % 90) : null;

            try {
                notificationRepository.save(DemoEntityFactory.notification(client, canal, statut, msg, dateEnvoi));
                count++;
            } catch (Exception e) {
                log.debug("DemoDataLoader: erreur notification client {}: {}", client.getCodeClient(), e.getMessage());
            }
        }

        log.info("DemoDataLoader: seedNotifications → {} notifications créées", count);
        return count;
    }

    // =========================================================================
    // PHASE 7 : BÉNÉFICIAIRES (100)
    // =========================================================================

    private int seedBeneficiaires(Map<String, Client> allClients, Map<String, Compte> comptes) {
        String[] noms = {"Kouadio", "Koffi", "Traore", "Coulibaly", "Diallo", "Bamba", "Kone", "Camara", "Sylla", "Fofana"};
        String[] prenoms = {"Jean", "Marie", "Awa", "Oumar", "Seydou", "Fanta", "Ibrahim", "Fatou", "Moussa", "Aissatou"};
        String[] liens = {"Conjoint", "Enfant", "Parent", "Frere_Soeur", "Ami"};

        List<String> numsComptes = new ArrayList<>(comptes.keySet());
        List<Client> clients = new ArrayList<>(allClients.values());
        int count = 0;

        for (int i = 0; i < 100 && i < clients.size(); i++) {
            Client client = clients.get(i);
            if (beneficiaireRepository.findByIdClientOrderByCreatedAtDesc(client.getIdClient()).size() >= 2) continue;

            String nomBenef = noms[i % noms.length];
            String prenomBenef = prenoms[(i * 3) % prenoms.length];
            String compteB = numsComptes.isEmpty() ? "CI23CB999999" + String.format("%06d", i)
                    : numsComptes.get((i * 7) % numsComptes.size());
            String lien = liens[i % liens.length];

            if (beneficiaireRepository.existsByIdClientAndCompteBeneficiaire(client.getIdClient(), compteB)) continue;

            try {
                beneficiaireRepository.save(DemoEntityFactory.beneficiaire(
                        client.getIdClient(), nomBenef, prenomBenef, compteB, "SOUTRA"));
                count++;
            } catch (Exception e) {
                log.debug("DemoDataLoader: erreur beneficiaire client {}: {}", client.getCodeClient(), e.getMessage());
            }
        }

        log.info("DemoDataLoader: seedBeneficiaires → {} bénéficiaires créés", count);
        return count;
    }

    // =========================================================================
    // PHASE 8 : DOCUMENTS CLIENT (150)
    // =========================================================================

    private int seedDocuments(Map<String, Client> allClients, Map<String, Utilisateur> utilisateurs) {
        Utilisateur admin = utilisateurs.getOrDefault("demo.admin",
                utilisateurs.isEmpty() ? null : utilisateurs.values().iterator().next());
        Long adminId = admin != null ? admin.getIdUser() : null;

        String[][] categories = {
            {"CNI_RECTO", "cni_recto.jpg", "image/jpeg", "250000"},
            {"CNI_VERSO", "cni_verso.jpg", "image/jpeg", "220000"},
            {"JUSTIFICATIF_DOMICILE", "justificatif_domicile.pdf", "application/pdf", "180000"},
            {"PHOTO_IDENTITE", "photo_identite.jpg", "image/jpeg", "150000"},
            {"BULLETIN_SALAIRE", "bulletin_salaire.pdf", "application/pdf", "200000"},
        };

        List<Client> clients = new ArrayList<>(allClients.values());
        int count = 0;

        for (int i = 0; i < 150 && i < clients.size() * categories.length; i++) {
            Client client = clients.get(i % clients.size());
            String[] cat = categories[i % categories.length];

            String chemin = "demo/docs/" + client.getCodeClient().toLowerCase().replace("-", "_")
                    + "/" + cat[1];
            Long taille = Long.parseLong(cat[3]) + (i * 1000L % 50000);
            LocalDateTime dateUpload = LocalDateTime.now().minusDays(30 + (i % 60));

            try {
                documentClientRepository.save(DemoEntityFactory.documentClient(
                        client.getIdClient(), cat[1], cat[0], cat[2], taille, chemin, adminId, dateUpload));
                count++;
            } catch (Exception e) {
                log.debug("DemoDataLoader: erreur document client {}: {}", client.getCodeClient(), e.getMessage());
            }
        }

        log.info("DemoDataLoader: seedDocuments → {} documents créés", count);
        return count;
    }

    // =========================================================================
    // MÉTHODES UTILITAIRES EXISTANTES (inchangées)
    // =========================================================================

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
        if (!Boolean.TRUE.equals(utilisateur.getActif())) { utilisateur.setActif(Boolean.TRUE); changed = true; }
        if (utilisateur.getCompteExpireLe() != null) { utilisateur.setCompteExpireLe(null); changed = true; }
        if (utilisateur.getCompteVerrouilleJusquAu() != null) { utilisateur.setCompteVerrouilleJusquAu(null); changed = true; }
        if (utilisateur.getNombreEchecsConnexion() == null || utilisateur.getNombreEchecsConnexion() != 0) { utilisateur.setNombreEchecsConnexion(0); changed = true; }
        if (utilisateur.getDernierEchecConnexion() != null) { utilisateur.setDernierEchecConnexion(null); changed = true; }
        if (utilisateur.getOtpChallengeId() != null) { utilisateur.setOtpChallengeId(null); changed = true; }
        if (utilisateur.getOtpHash() != null) { utilisateur.setOtpHash(null); changed = true; }
        if (utilisateur.getOtpExpireLe() != null) { utilisateur.setOtpExpireLe(null); changed = true; }
        if (utilisateur.getOtpTentativesRestantes() == null || utilisateur.getOtpTentativesRestantes() != 0) { utilisateur.setOtpTentativesRestantes(0); changed = true; }
        if (Boolean.TRUE.equals(utilisateur.getSecondFacteurActive())) { utilisateur.setSecondFacteurActive(Boolean.FALSE); changed = true; }
        if (utilisateur.getIdentifiantsExpirentLe() == null || utilisateur.getIdentifiantsExpirentLe().isBefore(LocalDateTime.now().plusDays(7))) { utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90)); changed = true; }
        if (utilisateur.getMotDePasseModifieLe() == null) { utilisateur.setMotDePasseModifieLe(LocalDateTime.now()); changed = true; }
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
        if (statutCompteRepository.findTopByCompte_IdCompteOrderByDateStatutDesc(compte.getIdCompte()).isPresent()) return;
        statutCompteRepository.save(DemoEntityFactory.statutCompteActif(compte));
    }

    private int seedTransactions(Map<String, Utilisateur> utilisateurs, Map<String, Compte> comptes) {
        int insertedTransactions = 0;
        for (TransactionSeed seed : DemoDataSeeds.TRANSACTIONS) {
            if (transactionRepository.existsByReferenceUnique(seed.referenceUnique())) continue;
            TypeTransaction typeTransaction = typeTransactionRepository.findByCodeTypeTransaction(seed.typeCode())
                    .orElseThrow(() -> new IllegalStateException("TypeTransaction " + seed.typeCode() + " not found in reference data. Did reference-data.sql run?"));
            Compte compteSource = seed.sourceAccountNumber() == null ? null : comptes.get(seed.sourceAccountNumber());
            Compte compteDestination = seed.destinationAccountNumber() == null ? null : comptes.get(seed.destinationAccountNumber());
            String login = seed.initiatorLogin() != null ? seed.initiatorLogin() : "demo.admin";
            Utilisateur initiateur = utilisateurs.getOrDefault(login, utilisateurs.get("demo.admin"));
            if (initiateur == null && !utilisateurs.isEmpty()) initiateur = utilisateurs.values().iterator().next();
            Transaction savedTransaction = transactionRepository.save(
                    DemoEntityFactory.transaction(seed, initiateur, typeTransaction, compteSource, compteDestination));
            createLignesEcriture(savedTransaction, seed, compteSource, compteDestination);
            insertedTransactions++;
        }
        return insertedTransactions;
    }

    private void seedCaisses(Map<String, Utilisateur> utilisateurs) {
        long[] soldesInitiaux = {5000000L, 3000000L, 4000000L, 2500000L, 3500000L, 2000000L, 4500000L, 3000000L};
        int idx = 0;
        for (Utilisateur u : utilisateurs.values()) {
            boolean isGuichetier = u.getRoles().stream()
                    .anyMatch(r -> "GUICHETIER".equals(r.getCodeRoleUtilisateur()));
            if (isGuichetier) {
                java.util.Optional<Caisse> openCaisseOpt = caisseRepository.findByUtilisateur_IdUserAndStatut(
                        u.getIdUser(), Caisse.StatutCaisse.OUVERTE);
                if (openCaisseOpt.isEmpty()) {
                    long solde = soldesInitiaux[idx % soldesInitiaux.length];
                    Caisse caisse = new Caisse();
                    caisse.setUtilisateur(u);
                    caisse.setSoldeOuverture(new BigDecimal(solde));
                    caisse.setSoldeCourant(new BigDecimal(solde));
                    caisse.setStatut(Caisse.StatutCaisse.OUVERTE);
                    caisse.setDateOuverture(LocalDateTime.now().minusDays(5));
                    caisseRepository.save(caisse);
                    log.info("DemoDataLoader: Caisse créée pour guichetier {} ({} FCFA)", u.getLogin(), solde);
                    idx++;
                }
            }
        }
    }

    private void createLignesEcriture(Transaction transaction, TransactionSeed seed,
                                       Compte compteSource, Compte compteDestination) {
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

    // =========================================================================
    // HELPERS
    // =========================================================================

    private static String numCompte(int clientIdx, int compteType) {
        // CI23CB + 12 chiffres
        return String.format("CI23CB%012d", (long) clientIdx * 10 + compteType);
    }

    private static BigDecimal soldeAleatoire(int seed, long min, long max) {
        long range = max - min;
        long val = min + Math.abs(seed * 137L + 51L) % range;
        // Arrondir au millier
        val = (val / 1000) * 1000;
        return BigDecimal.valueOf(Math.max(min, val));
    }

    private static String formatMontant(long montant) {
        return String.format("%,d", montant).replace(',', ' ');
    }
}
