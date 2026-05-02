package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.extension.CreditExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditExtensionController.class)
class CreditExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private CreditExtensionService creditService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerProduitsCredit() throws Exception {
        ProduitCredit produit = new ProduitCredit();
        produit.setIdProduitCredit(1L);
        produit.setCodeProduit("PRET_COMMERCE");
        produit.setLibelleProduit("Pret Commerce");

        when(creditService.listerProduitsCredit()).thenReturn(List.of(produit));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/produits")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeProduit").value("PRET_COMMERCE"));
    }

    @Test
    void shouldSoumettreDemandeCredit() throws Exception {
        DemandeCredit demande = TestDataFactory.createSampleDemandeCredit();
        when(creditService.soumettreDemandeCredit(any(DemandeCredit.class))).thenReturn(demande);

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/demandes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(demande)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.referenceDossier").value(demande.getReferenceDossier()));
    }

    @Test
    void shouldListerDemandesCredit() throws Exception {
        DemandeCredit demande = TestDataFactory.createSampleDemandeCredit();
        when(creditService.listerDemandesCredit()).thenReturn(List.of(demande));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/demandes")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetDemandeCreditById() throws Exception {
        DemandeCredit demande = TestDataFactory.createSampleDemandeCredit();
        when(creditService.rechercherDemandeParId(1L)).thenReturn(Optional.of(demande));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/demandes/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.referenceDossier").value(demande.getReferenceDossier()));
    }

    @Test
    void shouldAnalyserCredit() throws Exception {
        when(creditService.analyserDemande(1L)).thenReturn(Map.of(
            "score", 750,
            "decision", "FAVORABLE",
            "montantMaximum", 3000000
        ));

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/demandes/1/analyser")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("FAVORABLE"));
    }

    @Test
    void shouldApprouverCredit() throws Exception {
        Credit credit = TestDataFactory.createSampleCredit();
        when(creditService.approuverDemande(eq(1L), anyString())).thenReturn(credit);

        Map<String, String> approbationRequest = Map.of("commentaire", "Approuve par comite");
        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/demandes/1/approuver")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(approbationRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRejeterCredit() throws Exception {
        DemandeCredit demande = TestDataFactory.createSampleDemandeCredit();
        demande.setStatut("REJETE");
        when(creditService.rejeterDemande(eq(1L), anyString())).thenReturn(demande);

        Map<String, String> rejetRequest = Map.of("motif", "Score insuffisant");
        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/demandes/1/rejeter")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rejetRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("REJETE"));
    }

    @Test
    void shouldDebloquerCredit() throws Exception {
        Credit credit = TestDataFactory.createSampleCredit();
        credit.setStatut("DEBLOQUE");
        when(creditService.debloquerCredit(1L)).thenReturn(credit);

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/debloquer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("DEBLOQUE"));
    }

    @Test
    void shouldEffectuerRemboursement() throws Exception {
        RemboursementCredit remb = new RemboursementCredit();
        remb.setIdRemboursement(1L);
        remb.setMontant(new BigDecimal("200000.00"));
        when(creditService.effectuerRemboursement(eq(1L), any(BigDecimal.class)))
            .thenReturn(remb);

        Map<String, Object> rembRequest = Map.of("montant", 200000);
        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/remboursements")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rembRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerEcheances() throws Exception {
        EcheanceCredit echeance = new EcheanceCredit();
        echeance.setIdEcheance(1L);
        echeance.setNumeroEcheance(1);
        echeance.setMontantCapital(new BigDecimal("166667.00"));

        when(creditService.listerEcheances(1L)).thenReturn(List.of(echeance));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/1/echeances")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetImpayes() throws Exception {
        ImpayeCredit impaye = new ImpayeCredit();
        impaye.setIdImpaye(1L);
        impaye.setMontantImpaye(new BigDecimal("50000.00"));

        when(creditService.listerImpayes()).thenReturn(List.of(impaye));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/impayes")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldListerProvisions() throws Exception {
        ProvisionCredit provision = new ProvisionCredit();
        provision.setIdProvision(1L);
        provision.setMontantProvision(new BigDecimal("100000.00"));

        when(creditService.listerProvisions()).thenReturn(List.of(provision));

        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/provisions")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRestructureCredit() throws Exception {
        Credit credit = TestDataFactory.createSampleCredit();
        credit.setStatut("RESTRUCTURE");
        when(creditService.restructurerCredit(eq(1L), anyInt(), any(BigDecimal.class)))
            .thenReturn(credit);

        Map<String, Object> restructRequest = Map.of(
            "nouvelleDureeMois", 18,
            "nouveauMontant", 2500000
        );
        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/restructurer")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(restructRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldPasserEnPerte() throws Exception {
        Credit credit = TestDataFactory.createSampleCredit();
        credit.setStatut("PERTE");
        when(creditService.passerEnPerte(1L)).thenReturn(credit);

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/perte")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCloturerCredit() throws Exception {
        Credit credit = TestDataFactory.createSampleCredit();
        credit.setStatut("CLOTURE");
        when(creditService.cloturerCredit(1L)).thenReturn(credit);

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/cloture")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("CLOTURE"));
    }

    @Test
    void shouldAddGarantie() throws Exception {
        GarantieCredit garantie = new GarantieCredit();
        garantie.setIdGarantie(1L);
        garantie.setTypeGarantie("BIEN_IMMOBILIER");
        garantie.setValeurEstimee(new BigDecimal("5000000.00"));

        when(creditService.ajouterGarantie(eq(1L), any(GarantieCredit.class))).thenReturn(garantie);

        mockMvc.perform(post(ApiTestConstants.CREDITS_BASE + "/1/garanties")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(garantie)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.CREDITS_BASE + "/produits")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
