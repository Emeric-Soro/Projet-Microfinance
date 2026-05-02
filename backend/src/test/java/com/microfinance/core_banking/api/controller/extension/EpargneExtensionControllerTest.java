package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.extension.EpargneExtensionService;
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

@WebMvcTest(EpargneExtensionController.class)
class EpargneExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private EpargneExtensionService epargneService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerProduitsEpargne() throws Exception {
        ProduitEpargne produit = new ProduitEpargne();
        produit.setIdProduitEpargne(1L);
        produit.setCodeProduit("EPG_STANDARD");
        produit.setLibelleProduit("Epargne Standard");

        when(epargneService.listerProduitsEpargne()).thenReturn(List.of(produit));

        mockMvc.perform(get(ApiTestConstants.EPARGNE_BASE + "/produits")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeProduit").value("EPG_STANDARD"));
    }

    @Test
    void shouldOuvrirCompteEpargne() throws Exception {
        Epargne epargne = new Epargne();
        epargne.setIdEpargne(1L);
        epargne.setSoldeActuel(new BigDecimal("100000.00"));

        when(epargneService.ouvrirCompteEpargne(any(Epargne.class))).thenReturn(epargne);

        mockMvc.perform(post(ApiTestConstants.EPARGNE_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(epargne)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetEpargneById() throws Exception {
        Epargne epargne = new Epargne();
        epargne.setIdEpargne(1L);
        epargne.setSoldeActuel(new BigDecimal("500000.00"));
        when(epargneService.rechercherParId(1L)).thenReturn(Optional.of(epargne));

        mockMvc.perform(get(ApiTestConstants.EPARGNE_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.soldeActuel").value(5.0E5));
    }

    @Test
    void shouldCalculerInterets() throws Exception {
        Map<String, Object> interetResult = Map.of(
            "montantInterets", 12500.00,
            "tauxEffectif", 3.5
        );
        when(epargneService.calculerInterets(1L)).thenReturn(interetResult);

        mockMvc.perform(post(ApiTestConstants.EPARGNE_BASE + "/1/calcul-interets")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.montantInterets").value(12500.00));
    }

    @Test
    void shouldOuvrirDAT() throws Exception {
        DepotATerme dat = new DepotATerme();
        dat.setIdDepotATerme(1L);
        dat.setMontant(new BigDecimal("1000000.00"));
        dat.setDureeMois(12);

        when(epargneService.ouvrirDepotATerme(any(DepotATerme.class))).thenReturn(dat);

        mockMvc.perform(post(ApiTestConstants.EPARGNE_BASE + "/dat")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(dat)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldCloturerDAT() throws Exception {
        DepotATerme dat = new DepotATerme();
        dat.setIdDepotATerme(1L);
        dat.setStatut("CLOTURE");
        when(epargneService.cloturerDepotATerme(1L)).thenReturn(dat);

        mockMvc.perform(post(ApiTestConstants.EPARGNE_BASE + "/dat/1/cloture")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("CLOTURE"));
    }

    @Test
    void shouldListerDAT() throws Exception {
        DepotATerme dat = new DepotATerme();
        dat.setIdDepotATerme(1L);
        dat.setMontant(new BigDecimal("1000000.00"));

        when(epargneService.listerDepotsATerme()).thenReturn(List.of(dat));

        mockMvc.perform(get(ApiTestConstants.EPARGNE_BASE + "/dat")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.EPARGNE_BASE + "/produits")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
