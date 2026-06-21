package com.soutra.microfinance.config;

import com.soutra.microfinance.config.DemoDataSeeds.DemoUserSeed;
import com.soutra.microfinance.config.DemoDataSeeds.TransactionSeed;
import com.soutra.microfinance.entity.Agence;
import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.LigneEcriture;
import com.soutra.microfinance.entity.NiveauRisqueClient;
import com.soutra.microfinance.entity.ProduitEpargne;
import com.soutra.microfinance.entity.SensEcriture;
import com.soutra.microfinance.entity.StatutClient;
import com.soutra.microfinance.entity.StatutCompte;
import com.soutra.microfinance.entity.StatutKycClient;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.entity.Transaction;
import com.soutra.microfinance.entity.TypeCompte;
import com.soutra.microfinance.entity.TypePieceIdentite;
import com.soutra.microfinance.entity.TypeTransaction;
import com.soutra.microfinance.entity.Utilisateur;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

final class DemoEntityFactory {

    private DemoEntityFactory() {
    }

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
        client.setRevenuMensuel(seed.soldeInitial().divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP));
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
}
