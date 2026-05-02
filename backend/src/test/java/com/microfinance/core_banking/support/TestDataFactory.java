package com.microfinance.core_banking.support;

import com.microfinance.core_banking.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static Client createSampleClient() {
        Client c = new Client();
        c.setIdClient(1L);
        c.setCodeClient("CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        c.setNom("Dupont");
        c.setPrenom("Jean");
        c.setDateNaissance(LocalDate.of(1985, 6, 15));
        c.setAdresse("15 Rue de Paris, Dakar");
        c.setTelephone("+22177123456" + (int)(Math.random() * 100));
        c.setEmail("jean.dupont" + (int)(Math.random() * 1000) + "@email.com");
        c.setProfession("Commercant");
        c.setEmployeur("Independant");
        c.setTypePieceIdentite(TypePieceIdentite.CNI);
        c.setNumeroPieceIdentite("ID-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        c.setDateExpirationPieceIdentite(LocalDate.now().plusYears(5));
        c.setPaysNationalite("Senegal");
        c.setPaysResidence("Senegal");
        c.setPep(false);
        c.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        c.setStatutKyc(StatutKycClient.BROUILLON);
        c.setDateInscription(LocalDate.now());
        c.setStatutClient(createSampleStatutClient());
        return c;
    }

    public static StatutClient createSampleStatutClient() {
        StatutClient s = new StatutClient();
        s.setIdStatutClient(1L);
        s.setLibelleStatut("NOUVEAU");
        s.setDateStatut(LocalDateTime.now());
        return s;
    }

    public static Utilisateur createSampleUtilisateur() {
        Utilisateur u = new Utilisateur();
        u.setIdUser(1L);
        u.setLogin("user_" + UUID.randomUUID().toString().substring(0, 6));
        u.setPassword("{bcrypt}$2a$10$dummyhash");
        u.setActif(true);
        u.setClient(createSampleClient());
        u.setMotDePasseModifieLe(LocalDateTime.now());
        u.setIdentifiantsExpirentLe(LocalDateTime.now().plusMonths(6));
        u.setNombreEchecsConnexion(0);
        RoleUtilisateur role = new RoleUtilisateur();
        role.setIdRole(1L);
        role.setCodeRoleUtilisateur("ADMIN");
        role.setLibelleRole("Administrateur");
        role.setActif(true);
        u.setRoles(Set.of(role));
        return u;
    }

    private static java.util.Set<RoleUtilisateur> SetOf(RoleUtilisateur r) {
        return java.util.Collections.singleton(r);
    }

    public static Compte createSampleCompte() {
        Compte c = new Compte();
        c.setIdCompte(1L);
        c.setNumCompte("SN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4));
        c.setSolde(new BigDecimal("500000.00"));
        c.setDateOuverture(LocalDate.now());
        c.setDevise("XOF");
        c.setTauxInteret(new BigDecimal("3.5000"));
        c.setDecouvertAutorise(new BigDecimal("100000.00"));
        c.setClient(createSampleClient());
        TypeCompte tc = new TypeCompte();
        tc.setIdTypeCompte(1L);
        tc.setCodeTypeCompte("EPARGNE");
        tc.setLibelleTypeCompte("Compte Epargne");
        c.setTypeCompte(tc);
        return c;
    }

    public static Transaction createSampleTransaction() {
        Transaction t = new Transaction();
        t.setIdTransaction(1L);
        t.setReferenceUnique("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        t.setDateHeureTransaction(LocalDateTime.now());
        t.setMontantGlobal(new BigDecimal("100000.00"));
        t.setFrais(BigDecimal.ZERO);
        t.setStatutOperation(StatutOperation.EN_ATTENTE);
        t.setValidationSuperviseurRequise(false);
        t.setUtilisateur(createSampleUtilisateur());
        TypeTransaction tt = new TypeTransaction();
        tt.setIdTypeTransaction(1L);
        tt.setCodeTypeTransaction("DEPOT");
        tt.setLibelleTypeTransaction("Depot");
        t.setTypeTransaction(tt);
        t.setCompteSource(createSampleCompte());
        return t;
    }

    public static DemandeCredit createSampleDemandeCredit() {
        DemandeCredit d = new DemandeCredit();
        d.setIdDemandeCredit(1L);
        d.setReferenceDossier("DOS-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        d.setClient(createSampleClient());
        ProduitCredit pc = new ProduitCredit();
        pc.setIdProduitCredit(1L);
        pc.setCodeProduit("PRET_COMMERCE");
        pc.setLibelleProduit("Pret Commerce");
        d.setProduitCredit(pc);
        d.setMontantDemande(new BigDecimal("2000000.00"));
        d.setDureeMois(12);
        d.setObjetCredit("Financement stock marchandise");
        d.setStatut("BROUILLON");
        return d;
    }

    public static ActionEnAttente createSampleActionEnAttente() {
        ActionEnAttente a = new ActionEnAttente();
        a.setIdActionEnAttente(1L);
        a.setTypeAction("CREATION");
        a.setRessource("COMPTE");
        a.setReferenceRessource("REF-001");
        a.setStatut("EN_ATTENTE");
        a.setCommentaireMaker("Creation compte epargne");
        a.setMaker(createSampleUtilisateur());
        return a;
    }

    public static Credit createSampleCredit() {
        Credit c = new Credit();
        c.setIdCredit(1L);
        c.setReferenceCredit("CRE-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        c.setClient(createSampleClient());
        c.setMontantAccorde(new BigDecimal("2000000.00"));
        c.setDureeMois(12);
        c.setTauxInteret(new BigDecimal("8.5000"));
        c.setStatut("APPROUVE");
        c.setDateDebut(LocalDate.now());
        c.setDateFin(LocalDate.now().plusMonths(12));
        return c;
    }

    public static Epargne createSampleEpargne() {
        Epargne e = new Epargne();
        e.setIdEpargne(1L);
        e.setCompte(createSampleCompte());
        e.setSoldeActuel(new BigDecimal("500000.00"));
        e.setTauxRemuneration(new BigDecimal("3.5000"));
        e.setDateDernierInteret(LocalDate.now().minusMonths(1));
        e.setProduitEpargne(createSampleProduitEpargne());
        return e;
    }

    public static ProduitEpargne createSampleProduitEpargne() {
        ProduitEpargne p = new ProduitEpargne();
        p.setIdProduitEpargne(1L);
        p.setCodeProduit("EPG_STANDARD");
        p.setLibelleProduit("Epargne Standard");
        p.setTauxRemuneration(new BigDecimal("3.5000"));
        return p;
    }

    public static Caisse createSampleCaisse() {
        Caisse c = new Caisse();
        c.setIdCaisse(1L);
        c.setCodeCaisse("CAI-001");
        c.setLibelleCaisse("Caisse Principale");
        c.setSoldeTheorique(new BigDecimal("1000000.00"));
        return c;
    }

    public static SessionCaisse createSampleSessionCaisse() {
        SessionCaisse s = new SessionCaisse();
        s.setIdSessionCaisse(1L);
        s.setCaisse(createSampleCaisse());
        s.setDateOuverture(LocalDateTime.now());
        s.setStatut("OUVERTE");
        s.setSoldeOuverture(new BigDecimal("500000.00"));
        s.setUtilisateur(createSampleUtilisateur());
        return s;
    }

    public static BilletageCaisse createSampleBilletage() {
        BilletageCaisse b = new BilletageCaisse();
        b.setIdBilletage(1L);
        b.setSessionCaisse(createSampleSessionCaisse());
        b.setValeurFaciale(new BigDecimal("10000"));
        b.setQuantite(50);
        return b;
    }

    public static DepotATerme createSampleDepotATerme() {
        DepotATerme d = new DepotATerme();
        d.setIdDepotATerme(1L);
        d.setCompte(createSampleCompte());
        d.setMontant(new BigDecimal("1000000.00"));
        d.setDureeMois(12);
        d.setTauxInteret(new BigDecimal("6.5000"));
        d.setDateEcheance(LocalDate.now().plusMonths(12));
        d.setStatut("ACTIF");
        return d;
    }
}
