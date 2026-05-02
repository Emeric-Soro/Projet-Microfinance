package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.DigitalExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DigitalExtensionController.class)
class DigitalExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private DigitalExtensionService digitalService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldDepotMobileMoney() throws Exception {
        Map<String, Object> depotRequest = Map.of(
            "telephone", "+221770000001",
            "montant", 50000,
            "operateur", "ORANGE",
            "reference", "MM-001"
        );
        when(digitalService.effectuerDepotMobile(any())).thenReturn(Map.of(
            "statut", "SUCCES",
            "reference", "MM-001",
            "message", "Depot Mobile Money effectue"
        ));

        mockMvc.perform(post(ApiTestConstants.DIGITAL_BASE + "/mobile-money/depot")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(depotRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("SUCCES"));
    }

    @Test
    void shouldRetraitMobileMoney() throws Exception {
        Map<String, Object> retraitRequest = Map.of(
            "telephone", "+221770000001",
            "montant", 25000,
            "operateur", "ORANGE",
            "reference", "MM-002"
        );
        when(digitalService.effectuerRetraitMobile(any())).thenReturn(Map.of(
            "statut", "SUCCES",
            "reference", "MM-002"
        ));

        mockMvc.perform(post(ApiTestConstants.DIGITAL_BASE + "/mobile-money/retrait")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(retraitRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetHistoriqueMobile() throws Exception {
        when(digitalService.getHistoriqueMobileMoney()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.DIGITAL_BASE + "/mobile-money/historique")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.DIGITAL_BASE + "/mobile-money/depot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of())))
            .andExpect(status().isUnauthorized());
    }
}
