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
import com.soutra.microfinance.entity.MethodeCalculInteret;
import com.soutra.microfinance.entity.NiveauRisqueClient;
import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.ProduitCredit;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutCredit;
import com.soutra.microfinance.entity.StatutDemande;
import com.soutra.microfinance.entity.StatutEnvoi;
import com.soutra.microfinance.entity.StatutKycClient;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeCanal;
import com.soutra.microfinance.entity.TypeCompte;
import com.soutra.microfinance.entity.TypeGarantie;
import com.soutra.microfinance.entity.TypePieceIdentite;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Factory sans état pour la création des entités de démonstration SOUTRA.
 * Toutes les méthodes sont statiques et n'ont aucun effet de bord.
 */
final class DemoEntityFactory {

    private DemoEntityFactory() {
    }

    // =========================================================================
    // MÉTHODES EXISTANTES (inchangées)
    // =========================================================================

    static Client client(DemoUserSeed seed, StatutClient statutActif, Agence agencePrincipale) {
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
        String pays = seed.telephone().startsWith("+243") ? "RDC" : "Cote d'Ivoire";
        client.setPaysNationalite(pays);
        client.setPaysResidence(pays);
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
        client.setRevenuMensuel(seed.soldeInitial().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
        client.setSecteurActivite(seed.secteurActivite());
        return client;
    }

    static Utilisateur utilisateur(DemoUserSeed seed, Client client, Agence agencePrincipale, String password) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setClient(client);
        utilisateur.setLogin(seed.login());
        utilisateur.setPassword(password);
        utilisateur.setActif(true);
        utilisateur.setNombreEchecsConnexion(0);
        utilisateur.setSecondFacteurActive(false);
        utilisateur.setOtpTentativesRestantes(0);
        utilisateur.setMotDePasseModifieLe(LocalDateTime.now());
        utilisateur.setIdentifiantsExpirentLe(LocalDateTime.now().plusDays(90));
        utilisateur.setAgence(agencePrincipale);
        return utilisateur;
    }

    static Compte compte(DemoUserSeed seed, Client client, TypeCompte typeCompte, Agence agence, ProduitEpargne produit) {
        Compte compte = new Compte();
        compte.setNumCompte(seed.accountNumber());
        compte.setSolde(seed.soldeInitial());
        compte.setDateOuverture(LocalDate.now().minusDays(20));
        compte.setDevise("XOF");
        compte.setTauxInteret("EPARGNE".equals(seed.typeCompte()) ? new BigDecimal("3.5000") : BigDecimal.ZERO);
        compte.setDecouvertAutorise("COURANT".equals(seed.typeCompte()) ? new BigDecimal("50000.00") : BigDecimal.ZERO);
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setAgence(agence);
        compte.setProduitEpargne("EPARGNE".equals(seed.typeCompte()) ? produit : null);
        return compte;
    }

    static StatutCompte statutCompteActif(Compte compte) {
        StatutCompte statutCompte = new StatutCompte();
        statutCompte.setCompte(compte);
        statutCompte.setLibelleStatut("ACTIF");
        statutCompte.setDateStatut(LocalDateTime.now().minusDays(20));
        return statutCompte;
    }

    static Transaction transaction(TransactionSeed seed, Utilisateur initiateur, TypeTransaction type,
                                   Compte source, Compte destination) {
        Transaction transaction = new Transaction();
        transaction.setReferenceUnique(seed.referenceUnique());
        transaction.setDateHeureTransaction(LocalDateTime.now().minusDays(seed.daysAgo()));
        transaction.setMontantGlobal(seed.montant());
        transaction.setFrais(seed.frais());
        transaction.setStatutOperation(StatutOperation.EXECUTEE);
        transaction.setValidationSuperviseurRequise(false);
        transaction.setDateExecution(transaction.getDateHeureTransaction().plusMinutes(2));
        transaction.setUtilisateur(initiateur);
        transaction.setTypeTransaction(type);
        transaction.setCompteSource(source);
        transaction.setCompteDestination(destination);
        return transaction;
    }

    static LigneEcriture ligne(Transaction transaction, Compte compte, SensEcriture sens, BigDecimal montant) {
        LigneEcriture ligne = new LigneEcriture();
        ligne.setTransaction(transaction);
        ligne.setCompte(compte);
        ligne.setSens(sens);
        ligne.setMontant(montant);
        return ligne;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : CLIENTS PURS
    // =========================================================================

    /**
     * Crée un client pur (sans utilisateur ni compte associé).
     * Les statuts KYC et risque sont dérivés du seed.
     */
    static Client clientPur(DemoClientSeed seed, StatutClient statutClient, Agence agence) {
        Client client = new Client();
        client.setCodeClient(seed.codeClient());
        client.setNom(seed.nom());
        client.setPrenom(seed.prenom());
        client.setDateNaissance(seed.dateNaissance());
        client.setAdresse(seed.adresse());
        client.setTelephone(seed.telephone());
        client.setEmail(seed.email()); // peut être null
        client.setTypePieceIdentite(TypePieceIdentite.CNI);
        client.setNumeroPieceIdentite(seed.numeroPieceIdentite());
        client.setDateExpirationPieceIdentite(LocalDate.now().plusYears(5));
        client.setPhotoIdentiteUrl("demo/kyc/" + seed.codeClient().toLowerCase().replace("-", "_") + "/photo.jpg");
        client.setJustificatifDomicileUrl("demo/kyc/" + seed.codeClient().toLowerCase().replace("-", "_") + "/domicile.pdf");
        if ("Fonctionnaire".equals(seed.profession()) || "Comptable".equals(seed.profession())
                || "Ingenieur".equals(seed.profession()) || "Infirmiere".equals(seed.profession())
                || "Pharmacienne".equals(seed.profession()) || "Juriste".equals(seed.profession())
                || "Technicien".equals(seed.profession())) {
            client.setJustificatifRevenusUrl("demo/kyc/" + seed.codeClient().toLowerCase().replace("-", "_") + "/bulletin.pdf");
        }
        client.setProfession(seed.profession());
        client.setSecteurActivite(seed.secteurActivite());
        client.setEmployeur(seed.secteurActivite());
        client.setPaysNationalite("Cote d'Ivoire");
        client.setPaysResidence("Cote d'Ivoire");
        client.setRevenuMensuel(seed.revenuMensuel());
        client.setPep(false);

        // Niveau de risque
        client.setNiveauRisque(switch (seed.niveauRisque()) {
            case "MODERE" -> NiveauRisqueClient.MODERE;
            case "ELEVE" -> NiveauRisqueClient.ELEVE;
            case "CRITIQUE" -> NiveauRisqueClient.CRITIQUE;
            default -> NiveauRisqueClient.FAIBLE;
        });

        // Statut KYC
        StatutKycClient kyc = switch (seed.statutKyc()) {
            case "EN_ATTENTE" -> StatutKycClient.EN_ATTENTE;
            case "A_REVOIR" -> StatutKycClient.A_REVOIR;
            case "REJETE" -> StatutKycClient.REJETE;
            case "BROUILLON" -> StatutKycClient.BROUILLON;
            default -> StatutKycClient.VALIDE;
        };
        client.setStatutKyc(kyc);

        if (kyc == StatutKycClient.VALIDE) {
            client.setDateSoumissionKyc(LocalDate.now().minusDays(60));
            client.setDateValidationKyc(LocalDate.now().minusDays(45));
            client.setCommentaireKyc("Dossier KYC valide.");
            client.setValidateurKyc("demo.admin");
        } else if (kyc == StatutKycClient.EN_ATTENTE || kyc == StatutKycClient.A_REVOIR) {
            client.setDateSoumissionKyc(LocalDate.now().minusDays(10));
        }

        client.setDateInscription(LocalDate.now().minusDays(60));
        client.setStatutClient(statutClient);
        client.setAgence(agence);
        return client;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : COMPTES SUPPLÉMENTAIRES
    // =========================================================================

    /**
     * Crée un compte supplémentaire pour un client existant (Courant ou DAT).
     */
    static Compte compteSupplementaire(Client client, TypeCompte typeCompte, String numCompte,
                                       BigDecimal solde, BigDecimal tauxInteret, BigDecimal decouvert,
                                       Agence agence, ProduitEpargne produitEpargne, int daysAgo) {
        Compte compte = new Compte();
        compte.setNumCompte(numCompte);
        compte.setSolde(solde);
        compte.setDateOuverture(LocalDate.now().minusDays(daysAgo));
        compte.setDevise("XOF");
        compte.setTauxInteret(tauxInteret);
        compte.setDecouvertAutorise(decouvert);
        compte.setClient(client);
        compte.setTypeCompte(typeCompte);
        compte.setAgence(agence);
        compte.setProduitEpargne(produitEpargne);
        return compte;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : CRÉDITS
    // =========================================================================

    /**
     * Crée une demande de crédit.
     */
    static DemandeCredit demandeCredit(DemoCreditSeed seed, Client client, ProduitCredit produit,
                                       Utilisateur agentCredit) {
        DemandeCredit demande = new DemandeCredit();
        String dateStr = seed.dateDemande().replace("-", "");
        String seq = seed.codeClient().replaceAll("[^0-9]", "");
        if (seq.length() > 4) seq = seq.substring(seq.length() - 4);
        demande.setReferenceDemande("DEM-" + dateStr + "-" + seq);
        demande.setMontantDemande(seed.montantDemande());
        demande.setDureeSouhaitee(seed.dureeMois());
        demande.setObjetCredit(seed.objetCredit());
        demande.setDateDemande(LocalDate.parse(seed.dateDemande()));
        demande.setClient(client);
        demande.setProduitCredit(produit);
        demande.setAgentCredit(agentCredit);
        demande.setScoreClient(70 + (seed.codeClient().hashCode() & 0x1F)); // score fictif 70-101

        StatutDemande statut = switch (seed.statutDemande()) {
            case "EN_ETUDE" -> StatutDemande.EN_ETUDE;
            case "APPROUVEE" -> StatutDemande.APPROUVEE;
            case "REJETEE" -> StatutDemande.REJETEE;
            default -> StatutDemande.EN_ATTENTE;
        };
        demande.setStatutDemande(statut);

        if (statut == StatutDemande.APPROUVEE || statut == StatutDemande.REJETEE) {
            demande.setDateDecision(LocalDate.parse(seed.dateDemande()).plusDays(7).atStartOfDay());
        }
        if (statut == StatutDemande.REJETEE) {
            demande.setMotifRejet(motifRejet(seed));
        }
        return demande;
    }

    private static String motifRejet(DemoCreditSeed seed) {
        int h = Math.abs(seed.codeClient().hashCode() % 5);
        return switch (h) {
            case 0 -> "Revenus insuffisants : revenu mensuel < 3x l'échéance calculée.";
            case 1 -> "Historique de défaut de paiement constaté.";
            case 2 -> "Garantie insuffisante : valeur < 50% du montant demandé.";
            case 3 -> "Niveau de risque client trop élevé.";
            default -> "Dossier incomplet : pièces justificatives manquantes.";
        };
    }

    /**
     * Crée un crédit actif à partir d'une demande approuvée.
     */
    static Credit credit(DemandeCredit demande, StatutCredit statutCredit, Compte compteDecaissement,
                         BigDecimal tauxAnnuel, int dureeMois, LocalDate dateDecaissement,
                         String codeClient) {
        Credit credit = new Credit();
        // Utilise le codeClient nettoyé pour garantir l'unicité de la référence
        String clientSuffix = codeClient.replaceAll("[^0-9A-Za-z]", "");
        if (clientSuffix.length() > 6) clientSuffix = clientSuffix.substring(clientSuffix.length() - 6);
        String ref = "CRD-" + dateDecaissement.toString().replace("-", "") + "-" + clientSuffix;
        credit.setReferenceCredit(ref);
        credit.setMontantAccorde(demande.getMontantDemande());
        credit.setTauxInteretAnnuel(tauxAnnuel);
        credit.setDureeMois(dureeMois);
        credit.setMethodeCalcul(MethodeCalculInteret.DEGRESSIF);
        credit.setFraisDossier(demande.getMontantDemande().multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP));
        credit.setDateDecaissement(dateDecaissement);
        credit.setDateFinPrevue(dateDecaissement.plusMonths(dureeMois));
        credit.setClient(demande.getClient());
        credit.setProduitCredit(demande.getProduitCredit());
        credit.setStatutCredit(statutCredit);
        credit.setCompteDecaissement(compteDecaissement);
        credit.setDemandeCredit(demande);

        // Montant restant dû en fonction du statut
        BigDecimal montant = demande.getMontantDemande();
        credit.setMontantRestantDu(switch (statutCredit.getCodeStatut()) {
            case "SOLDE" -> BigDecimal.ZERO;
            case "EN_COURS" -> montant.multiply(new BigDecimal("0.65")).setScale(2, RoundingMode.HALF_UP);
            case "EN_RETARD" -> montant.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
            case "SOUFFRANCE", "CONTENTIEUX" -> montant.multiply(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP);
            default -> montant;
        });
        return credit;
    }

    /**
     * Génère une ligne d'échéancier (tableau d'amortissement dégressif).
     */
    static Echeance echeance(Credit credit, int numero, LocalDate dateEcheance,
                             BigDecimal montantCapital, BigDecimal montantInteret,
                             boolean estPayee, LocalDate datePaiement, BigDecimal penalite) {
        Echeance ech = new Echeance();
        ech.setCredit(credit);
        ech.setNumeroEcheance(numero);
        ech.setDateEcheance(dateEcheance);
        ech.setMontantCapital(montantCapital);
        ech.setMontantInteret(montantInteret);
        ech.setMontantTotal(montantCapital.add(montantInteret).setScale(2, RoundingMode.HALF_UP));
        ech.setMontantPenalite(penalite != null ? penalite : BigDecimal.ZERO);
        ech.setEstPayee(estPayee);
        if (estPayee) {
            ech.setMontantPaye(ech.getMontantTotal().add(ech.getMontantPenalite()));
            ech.setDatePaiement(datePaiement != null ? datePaiement : dateEcheance);
        } else {
            ech.setMontantPaye(BigDecimal.ZERO);
        }
        return ech;
    }

    /**
     * Crée une garantie associée à un crédit.
     */
    static Garantie garantie(Credit credit, TypeGarantie type, String description, BigDecimal valeur) {
        Garantie g = new Garantie();
        g.setCredit(credit);
        g.setTypeGarantie(type);
        g.setDescription(description);
        g.setValeurEstimee(valeur);
        g.setEstActive(true);
        return g;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : TRANSACTIONS HISTORIQUES
    // =========================================================================

    /**
     * Crée une transaction avec une date exacte (pour l'historique).
     */
    static Transaction transactionHistorique(String reference, LocalDateTime dateHeure,
                                             BigDecimal montant, BigDecimal frais,
                                             StatutOperation statut, boolean validationRequise,
                                             Utilisateur initiateur, TypeTransaction type,
                                             Compte source, Compte destination) {
        Transaction t = new Transaction();
        t.setReferenceUnique(reference);
        t.setDateHeureTransaction(dateHeure);
        t.setMontantGlobal(montant);
        t.setFrais(frais != null ? frais : BigDecimal.ZERO);
        t.setStatutOperation(statut);
        t.setValidationSuperviseurRequise(validationRequise);
        if (statut == StatutOperation.EXECUTEE) {
            t.setDateExecution(dateHeure.plusMinutes(2));
        }
        t.setUtilisateur(initiateur);
        t.setTypeTransaction(type);
        t.setCompteSource(source);
        t.setCompteDestination(destination);
        return t;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : NOTIFICATIONS
    // =========================================================================

    /**
     * Crée une notification pour un client.
     */
    static Notification notification(Client client, TypeCanal canal, StatutEnvoi statut,
                                     String message, LocalDate dateEnvoi) {
        Notification n = new Notification();
        n.setClient(client);
        n.setTypeCanal(canal);
        n.setStatutEnvoi(statut);
        n.setMessage(message);
        n.setLu(false);
        if (dateEnvoi != null) {
            n.setDateEnvoi(dateEnvoi);
        }
        return n;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : BÉNÉFICIAIRES
    // =========================================================================

    /**
     * Crée un bénéficiaire lié à un client.
     */
    static Beneficiaire beneficiaire(Long idClient, String nom, String prenom,
                                     String compteBeneficiaire, String banque) {
        Beneficiaire b = new Beneficiaire();
        b.setIdClient(idClient);
        b.setNom(nom);
        b.setPrenom(prenom);
        b.setCompteBeneficiaire(compteBeneficiaire);
        b.setBanque(banque != null ? banque : "SOUTRA");
        return b;
    }

    // =========================================================================
    // NOUVELLES MÉTHODES : DOCUMENTS CLIENT
    // =========================================================================

    /**
     * Crée un document client.
     */
    static DocumentClient documentClient(Long idClient, String nomFichier, String categorie,
                                         String typeMime, Long taille, String chemin,
                                         Long uploadedBy, LocalDateTime dateUpload) {
        DocumentClient d = new DocumentClient();
        d.setIdClient(idClient);
        d.setNomFichier(nomFichier);
        d.setCategorie(categorie);
        d.setTypeMime(typeMime);
        d.setTailleOctets(taille);
        d.setCheminStockage(chemin);
        d.setUploadedBy(uploadedBy);
        d.setDateUpload(dateUpload);
        return d;
    }
}
