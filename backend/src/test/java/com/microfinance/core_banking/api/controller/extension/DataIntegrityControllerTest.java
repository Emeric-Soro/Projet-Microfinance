package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.config.DataIntegrityService;
import com.microfinance.core_banking.service.config.FeatureInventoryService;
import com.microfinance.core_banking.service.config.SystemAuditLogService;
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

@WebMvcTest(DataIntegrityController.class)
class DataIntegrityControllerTest extends AbstractControllerTest {

    @MockBean
    private DataIntegrityService diService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldCheckIntegrity() throws Exception {
        when(diService.verifierIntegrite()).thenReturn(Map.of(
            "statut", "OK",
            "anomalies", java.util.List.of(),
            "dateVerification", "2026-05-02"
        ));

        mockMvc.perform(get(ApiTestConstants.DATA_INTEGRITY_BASE + "/check")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("OK"));
    }

    @Test
    void shouldRepairIntegrity() throws Exception {
        when(diService.reparerIntegrite()).thenReturn(Map.of(
            "statut", "REPAIRED",
            "corrections", 3
        ));

        mockMvc.perform(post(ApiTestConstants.DATA_INTEGRITY_BASE + "/repair")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.DATA_INTEGRITY_BASE + "/check")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
