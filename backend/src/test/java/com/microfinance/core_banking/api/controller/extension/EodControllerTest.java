package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.EodBatchService;
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

@WebMvcTest(EodController.class)
class EodControllerTest extends AbstractControllerTest {

    @MockBean
    private EodBatchService eodService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldRunEod() throws Exception {
        Map<String, Object> eodResult = Map.of(
            "statut", "COMPLETED",
            "jobId", 1L,
            "dateTraitement", "2026-05-02",
            "resume", Map.of(
                "interetsCrediteurs", 150,
                "interetsDebiteurs", 30,
                "provisions", 10,
                "impayes", 5,
                "clotures", 0
            )
        );
        when(eodService.executerEod()).thenReturn(eodResult);

        mockMvc.perform(post(ApiTestConstants.EOD_BASE + "/executer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("COMPLETED"));
    }

    @Test
    void shouldGetEodStatus() throws Exception {
        when(eodService.getDernierEodStatus()).thenReturn(Map.of(
            "statut", "COMPLETED",
            "dateDernierRun", "2026-05-01",
            "dureeExecutionSec", 45
        ));

        mockMvc.perform(get(ApiTestConstants.EOD_BASE + "/statut")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRestartEod() throws Exception {
        when(eodService.redemarrerEod()).thenReturn(Map.of(
            "statut", "RESTARTED",
            "message", "EOD redemarre"
        ));

        mockMvc.perform(post(ApiTestConstants.EOD_BASE + "/restart")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("RESTARTED"));
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.EOD_BASE + "/executer")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
