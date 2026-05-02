package com.microfinance.core_banking.api.controller.compte;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.compte.CompteService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompteController.class)
class CompteControllerTest extends AbstractControllerTest {

    @MockBean
    private CompteService compteService;

    private String adminToken;
    private Compte sampleCompte;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        sampleCompte = TestDataFactory.createSampleCompte();
    }

    @Test
    void shouldOpenCompte() throws Exception {
        when(compteService.ouvrirCompte(any(Compte.class))).thenReturn(sampleCompte);

        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleCompte)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numCompte").value(sampleCompte.getNumCompte()));
    }

    @Test
    void shouldGetSolde() throws Exception {
        when(compteService.consulterSolde("SN001")).thenReturn(new BigDecimal("500000.00"));

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE + "/SN001/solde")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(5.0E5));
    }

    @Test
    void shouldGetCompteByNumero() throws Exception {
        when(compteService.rechercherParNumCompte("SN001")).thenReturn(Optional.of(sampleCompte));

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE + "/SN001")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numCompte").value(sampleCompte.getNumCompte()));
    }

    @Test
    void shouldReturn404WhenCompteNotFound() throws Exception {
        when(compteService.rechercherParNumCompte("INVALID")).thenReturn(Optional.empty());

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE + "/INVALID")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllComptes() throws Exception {
        when(compteService.listerTousComptes()).thenReturn(List.of(sampleCompte));

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].numCompte").value(sampleCompte.getNumCompte()));
    }

    @Test
    void shouldGetComptesByClientId() throws Exception {
        when(compteService.listerComptesParClient(1L)).thenReturn(List.of(sampleCompte));

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE + "/client/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSetDecouvert() throws Exception {
        Map<String, Object> decouvertRequest = Map.of(
            "montant", 200000,
            "motif", "decouvert autorise"
        );
        when(compteService.demanderDecouvert(eq("SN001"), any(BigDecimal.class), anyString()))
            .thenReturn(sampleCompte);

        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE + "/SN001/decouvert")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(decouvertRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldClotureCompte() throws Exception {
        when(compteService.cloturerCompte("SN001")).thenReturn(sampleCompte);

        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE + "/SN001/cloture")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldBlockCompteViaMakerChecker() throws Exception {
        Map<String, String> blockRequest = Map.of("motif", "Fraude suspectee");
        when(compteService.demanderBlocage(eq("SN001"), anyString()))
            .thenReturn(sampleCompte);

        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE + "/SN001/bloquer")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(blockRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldUnblockCompteViaMakerChecker() throws Exception {
        Map<String, String> unblockRequest = Map.of("motif", "Levee blocage");
        when(compteService.demanderDeblocage(eq("SN001"), anyString()))
            .thenReturn(sampleCompte);

        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE + "/SN001/debloquer")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(unblockRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetCompteStatuts() throws Exception {
        StatutCompte statut = new StatutCompte();
        statut.setLibelleStatut("ACTIF");
        when(compteService.listerStatuts("SN001")).thenReturn(List.of(statut));

        mockMvc.perform(get(ApiTestConstants.COMPTES_BASE + "/SN001/statuts")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.COMPTES_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleCompte)))
            .andExpect(status().isUnauthorized());
    }
}
