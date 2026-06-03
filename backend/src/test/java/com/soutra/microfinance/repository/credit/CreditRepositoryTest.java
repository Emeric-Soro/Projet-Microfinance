package com.soutra.microfinance.repository.credit;

import com.soutra.microfinance.entity.*;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.client.StatutClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class, properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CreditRepositoryTest {

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private DemandeCreditRepository demandeCreditRepository;

    @Autowired
    private ProduitCreditRepository produitCreditRepository;

    @Autowired
    private StatutCreditRepository statutCreditRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private StatutClientRepository statutClientRepository;

    @Test
    void shouldFindCreditByStatutCode() {
        Client client = createClient();
        ProduitCredit produit = createProduitCredit();
        StatutCredit statut = createStatutCredit("APPROUVE", "Approuve");
        createCredit(client, produit, statut, "CRD-TEST-001");

        Page<Credit> page = creditRepository.findByStatutCredit_CodeStatut("APPROUVE", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(page.getContent().get(0).getReferenceCredit()).isEqualTo("CRD-TEST-001");
    }

    @Test
    void shouldCountCredits() {
        Client client = createClient();
        ProduitCredit produit = createProduitCredit();
        StatutCredit statut = createStatutCredit("EN_COURS", "En cours");
        createCredit(client, produit, statut, "CRD-COUNT-001");
        createCredit(client, produit, statut, "CRD-COUNT-002");

        long count = creditRepository.count();

        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldFindDemandeByStatut() {
        Client client = createClient();
        ProduitCredit produit = createProduitCredit();
        createDemandeCredit(client, produit, "DEM-TEST-001", StatutDemande.EN_ATTENTE);

        Page<DemandeCredit> page = demandeCreditRepository.findByStatutDemande(
                StatutDemande.EN_ATTENTE, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldFindProduitCreditByCode() {
        createProduitCredit();

        var produit = produitCreditRepository.findByCodeProduit("MC-COMMERCE");

        assertThat(produit).isPresent();
        assertThat(produit.get().getLibelle()).isEqualTo("Micro-Commerce");
    }

    private Client createClient() {
        StatutClient statut = statutClientRepository.save(buildStatutClient("ACTIF"));
        Client client = new Client();
        client.setCodeClient("CLT-" + System.nanoTime());
        client.setNom("Test");
        client.setPrenom("Client");
        client.setDateInscription(LocalDate.now());
        client.setStatutClient(statut);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setStatutKyc(StatutKycClient.VALIDE);
        client.setPep(false);
        return clientRepository.save(client);
    }

    private ProduitCredit createProduitCredit() {
        ProduitCredit produit = new ProduitCredit();
        produit.setCodeProduit("MC-COMMERCE");
        produit.setLibelle("Micro-Commerce");
        produit.setTauxInteretAnnuel(new BigDecimal("0.12"));
        produit.setDureeMinMois(3);
        produit.setDureeMaxMois(24);
        produit.setMontantMin(new BigDecimal("50000"));
        produit.setMontantMax(new BigDecimal("5000000"));
        produit.setMethodeCalcul(MethodeCalculInteret.CONSTANT);
        produit.setFraisDossierPourcentage(new BigDecimal("1.0"));
        produit.setEstActif(true);
        return produitCreditRepository.save(produit);
    }

    private StatutCredit createStatutCredit(String code, String libelle) {
        StatutCredit statut = new StatutCredit();
        statut.setCodeStatut(code);
        statut.setLibelle(libelle);
        return statutCreditRepository.save(statut);
    }

    private Credit createCredit(Client client, ProduitCredit produit, StatutCredit statut, String ref) {
        Credit credit = new Credit();
        credit.setReferenceCredit(ref);
        credit.setClient(client);
        credit.setProduitCredit(produit);
        credit.setStatutCredit(statut);
        credit.setMontantAccorde(new BigDecimal("500000"));
        credit.setMontantRestantDu(new BigDecimal("500000"));
        credit.setTauxInteretAnnuel(new BigDecimal("0.12"));
        credit.setDureeMois(12);
        credit.setMethodeCalcul(MethodeCalculInteret.CONSTANT);
        credit.setFraisDossier(new BigDecimal("5000"));
        credit.setVersion(0);
        return creditRepository.save(credit);
    }

    private DemandeCredit createDemandeCredit(Client client, ProduitCredit produit, String ref, StatutDemande statut) {
        DemandeCredit demande = new DemandeCredit();
        demande.setReferenceDemande(ref);
        demande.setClient(client);
        demande.setProduitCredit(produit);
        demande.setMontantDemande(new BigDecimal("500000"));
        demande.setDureeSouhaitee(12);
        demande.setObjetCredit("Test");
        demande.setDateDemande(LocalDate.now());
        demande.setStatutDemande(statut);
        return demandeCreditRepository.save(demande);
    }

    private StatutClient buildStatutClient(String libelle) {
        StatutClient statut = new StatutClient();
        statut.setLibelleStatut(libelle);
        statut.setDateStatut(LocalDateTime.now());
        return statut;
    }
}
