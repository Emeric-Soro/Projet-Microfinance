package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.config.FeatureInventoryService;
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

@WebMvcTest(FeatureInventoryController.class)
class FeatureInventoryControllerTest extends AbstractControllerTest {

    @MockBean
    private FeatureInventoryService featureService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerFonctionnalites() throws Exception {
        when(featureService.listerFonctionnalites()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.FEATURES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldActiverFonctionnalite() throws Exception {
        when(featureService.activerFonctionnalite("EPARGNE_DAT")).thenReturn(Map.of(
            "code", "EPARGNE_DAT",
            "actif", true
        ));

        mockMvc.perform(post(ApiTestConstants.FEATURES_BASE + "/EPARGNE_DAT/activer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.actif").value(true));
    }

    @Test
    void shouldDesactiverFonctionnalite() throws Exception {
        when(featureService.desactiverFonctionnalite("EPARGNE_DAT")).thenReturn(Map.of(
            "code", "EPARGNE_DAT",
            "actif", false
        ));

        mockMvc.perform(post(ApiTestConstants.FEATURES_BASE + "/EPARGNE_DAT/desactiver")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.FEATURES_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
