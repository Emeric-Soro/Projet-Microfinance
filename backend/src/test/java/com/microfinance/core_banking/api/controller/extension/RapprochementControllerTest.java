package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.RapprochementService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RapprochementController.class)
class RapprochementControllerTest extends AbstractControllerTest {

    @MockBean
    private RapprochementService rapprochementService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldEffectuerRapprochement() throws Exception {
        when(rapprochementService.effectuerRapprochement(any(LocalDate.class))).thenReturn(Map.of(
            "date", "2026-05-02",
            "totalTransactions", 250,
            "totalEcritures", 250,
            "ecarts", 0,
            "statut", "CONFORME"
        ));

        mockMvc.perform(post(ApiTestConstants.RAPPROCHEMENT_BASE + "/executer?date=2026-05-02")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("CONFORME"));
    }

    @Test
    void shouldGetHistoriqueRapprochement() throws Exception {
        when(rapprochementService.listerRapprochements()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.RAPPROCHEMENT_BASE + "/historique")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.RAPPROCHEMENT_BASE + "/executer")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
