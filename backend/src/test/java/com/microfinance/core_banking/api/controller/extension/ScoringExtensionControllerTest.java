package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.ScoringExtensionService;
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

@WebMvcTest(ScoringExtensionController.class)
class ScoringExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private ScoringExtensionService scoringService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldCalculerScore() throws Exception {
        Map<String, Object> scoreResult = Map.of(
            "score", 750,
            "categorie", "EXCELLENT",
            "probabiliteDefaut", 2.5,
            "facteurs", Map.of(
                "anciennete", 85,
                "flux", 90,
                "garanties", 75,
                "historique", 80
            )
        );
        when(scoringService.calculerScoreClient(1L)).thenReturn(scoreResult);

        mockMvc.perform(post(ApiTestConstants.SCORING_BASE + "/calculer/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(750))
            .andExpect(jsonPath("$.categorie").value("EXCELLENT"));
    }

    @Test
    void shouldGetGrilleScoring() throws Exception {
        when(scoringService.getGrilleScoring()).thenReturn(Map.of(
            "criteres", java.util.List.of()
        ));

        mockMvc.perform(get(ApiTestConstants.SCORING_BASE + "/grille")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.SCORING_BASE + "/calculer/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
