package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
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

@WebMvcTest(ComptabiliteExtensionController.class)
class ComptabiliteExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private ComptabiliteExtensionService comptabiliteService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldBootstrapComptable() throws Exception {
        when(comptabiliteService.bootstrapPlanComptable()).thenReturn(Map.of(
            "classes", 8, "comptes", 120, "statut", "OK"
        ));

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/bootstrap")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("OK"));
    }

    @Test
    void shouldGetAllClasses() throws Exception {
        ClasseComptable classe = new ClasseComptable();
        classe.setCodeClasse("1");
        classe.setLibelleClasse("Capital");
        when(comptabiliteService.listerClasses()).thenReturn(List.of(classe));

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/classes")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeClasse").value("1"));
    }

    @Test
    void shouldCreateCompteComptable() throws Exception {
        CompteComptable compte = new CompteComptable();
        compte.setNumeroCompte("601000");
        compte.setLibelleCompte("Achats");
        when(comptabiliteService.creerCompteComptable(any(CompteComptable.class))).thenReturn(compte);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/comptes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(compte)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateJournal() throws Exception {
        JournalComptable journal = new JournalComptable();
        journal.setCodeJournal("CAI");
        journal.setLibelleJournal("Caisse");
        when(comptabiliteService.creerJournal(any(JournalComptable.class))).thenReturn(journal);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/journaux")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(journal)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codeJournal").value("CAI"));
    }

    @Test
    void shouldCreateSchemaComptable() throws Exception {
        SchemaComptable schema = new SchemaComptable();
        schema.setCodeOperation("DEPOT_CASH");
        schema.setCompteDebit("571000");
        schema.setCompteCredit("251000");
        when(comptabiliteService.creerSchema(any(SchemaComptable.class))).thenReturn(schema);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/schemas")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(schema)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldTesterSchema() throws Exception {
        Map<String, Object> testRequest = Map.of(
            "codeOperation", "DEPOT_CASH",
            "montant", 100000,
            "frais", 2500
        );
        Map<String, Object> testResult = Map.of(
            "equilibree", true,
            "totalDebit", 102500.00,
            "totalCredit", 102500.00,
            "lignes", List.of()
        );
        when(comptabiliteService.testerSchemaComptable(any())).thenReturn(testResult);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/schemas/test")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.equilibree").value(true));
    }

    @Test
    void shouldCreateEcriture() throws Exception {
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setIdEcritureComptable(1L);
        ecriture.setReferencePiece("EC-2026-001");
        when(comptabiliteService.creerEcriture(any(EcritureComptable.class))).thenReturn(ecriture);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/ecritures")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(ecriture)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetGrandLivre() throws Exception {
        LigneEcritureComptable ligne = new LigneEcritureComptable();
        ligne.setSens("DEBIT");
        ligne.setMontant(new BigDecimal("1000.00"));
        when(comptabiliteService.consulterGrandLivre("601000", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)))
            .thenReturn(List.of(ligne));

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/grand-livre/601000?debut=2026-01-01&fin=2026-12-31")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetBalance() throws Exception {
        Map<String, Object> balanceEntry = Map.of(
            "numeroCompte", "601000",
            "totalDebit", 100000.00,
            "totalCredit", 50000.00,
            "soldeDebiteur", 50000.00
        );
        when(comptabiliteService.genererBalance(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.of(balanceEntry));

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/balance?debut=2026-01-01&fin=2026-12-31")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRunControles() throws Exception {
        Map<String, Object> controleResult = Map.of(
            "totalEcritures", 150,
            "totalLignes", 300,
            "equilibreGlobal", true,
            "ecrituresDesequilibrees", List.of(),
            "ecrituresSansLignes", 0
        );
        when(comptabiliteService.controlesComptables(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(controleResult);

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/controles?debut=2026-01-01&fin=2026-12-31")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.equilibreGlobal").value(true));
    }

    @Test
    void shouldClotureExercice() throws Exception {
        ClotureComptable cloture = new ClotureComptable();
        cloture.setIdCloture(1L);
        cloture.setDateCloture(LocalDate.of(2026, 12, 31));
        when(comptabiliteService.effectuerCloture(2026)).thenReturn(cloture);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/clotures/2026")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/classes")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
