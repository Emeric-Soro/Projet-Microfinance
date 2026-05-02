package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.TresorerieService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TresorerieController.class)
class TresorerieControllerTest extends AbstractControllerTest {

    @MockBean
    private TresorerieService tresorerieService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldGetSyntheseTresorerie() throws Exception {
        when(tresorerieService.getSyntheseTresorerie()).thenReturn(Map.of(
            "soldeTotal", 150000000.00,
            "encaissementJour", 25000000.00,
            "decaissementJour", 18000000.00,
            "positionNet", 7000000.00,
            "ratioLiquidite", 1.5
        ));

        mockMvc.perform(get(ApiTestConstants.TRESORERIE_BASE + "/synthese")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.soldeTotal").value(1.5E8));
    }

    @Test
    void shouldGetPrevisionTresorerie() throws Exception {
        when(tresorerieService.getPrevisionTresorerie(7)).thenReturn(Map.of(
            "previsions", java.util.List.of()
        ));

        mockMvc.perform(get(ApiTestConstants.TRESORERIE_BASE + "/previsions?jours=7")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetFluxTresorerie() throws Exception {
        when(tresorerieService.getFluxTresorerie()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.TRESORERIE_BASE + "/flux")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldTransfererTresorerie() throws Exception {
        Map<String, Object> transfert = Map.of(
            "montant", 5000000,
            "source", "CAISSE_PRINCIPALE",
            "destination", "CAISSE_AGENCE",
            "motif", "Reapprovisionnement"
        );
        when(tresorerieService.transfererFonds(any())).thenReturn(Map.of(
            "id", 1,
            "statut", "EXECUTE",
            "reference", "TRF-001"
        ));

        mockMvc.perform(post(ApiTestConstants.TRESORERIE_BASE + "/transferts")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transfert)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.TRESORERIE_BASE + "/synthese")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
