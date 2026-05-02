package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.SupportEntrepriseService;
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

@WebMvcTest(SupportEntrepriseController.class)
class SupportEntrepriseControllerTest extends AbstractControllerTest {

    @MockBean
    private SupportEntrepriseService supportService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldCreerTicket() throws Exception {
        Map<String, String> ticketRequest = Map.of(
            "sujet", "Probleme connexion",
            "description", "Impossible de se connecter a l'application",
            "priorite", "HAUTE"
        );
        when(supportService.creerTicket(any())).thenReturn(Map.of(
            "id", 1L,
            "reference", "TKT-001",
            "statut", "OUVERT"
        ));

        mockMvc.perform(post(ApiTestConstants.SUPPORT_BASE + "/tickets")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(ticketRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statut").value("OUVERT"));
    }

    @Test
    void shouldListerTickets() throws Exception {
        when(supportService.listerTickets()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.SUPPORT_BASE + "/tickets")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldResoudreTicket() throws Exception {
        Map<String, String> resolutionRequest = Map.of(
            "solution", "Redemarrage serveur effectue",
            "statut", "RESOLU"
        );
        when(supportService.resoudreTicket(eq(1L), any())).thenReturn(Map.of(
            "id", 1L, "statut", "RESOLU"
        ));

        mockMvc.perform(put(ApiTestConstants.SUPPORT_BASE + "/tickets/1/resoudre")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(resolutionRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.SUPPORT_BASE + "/tickets")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
