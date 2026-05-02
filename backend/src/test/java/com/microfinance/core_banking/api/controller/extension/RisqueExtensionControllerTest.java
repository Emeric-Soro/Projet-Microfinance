package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.RisqueExtensionService;
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

@WebMvcTest(RisqueExtensionController.class)
class RisqueExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private RisqueExtensionService risqueService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldGetRapportRisque() throws Exception {
        when(risqueService.genererRapportRisqueGlobal()).thenReturn(Map.of(
            "portefeuilleTotal", 500000000,
            "encoursSain", 450000000,
            "encoursImpaye", 50000000,
            "tauxImpayes", 10.0,
            "provisions", 15000000,
            "ratioCouverture", 30.0,
            "expositionMax", 50000000
        ));

        mockMvc.perform(get(ApiTestConstants.RISQUE_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tauxImpayes").value(10.0));
    }

    @Test
    void shouldGetRapportParAgence() throws Exception {
        when(risqueService.genererRapportRisqueParAgence(1L)).thenReturn(Map.of(
            "agenceId", 1,
            "encours", 75000000,
            "tauxImpayes", 5.0
        ));

        mockMvc.perform(get(ApiTestConstants.RISQUE_BASE + "/agence/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.RISQUE_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
