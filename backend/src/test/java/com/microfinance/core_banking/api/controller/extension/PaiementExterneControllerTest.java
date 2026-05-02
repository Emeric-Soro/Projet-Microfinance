package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.PaiementExterneService;
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

@WebMvcTest(PaiementExterneController.class)
class PaiementExterneControllerTest extends AbstractControllerTest {

    @MockBean
    private PaiementExterneService paiementService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldInitierPaiement() throws Exception {
        Map<String, Object> paiementRequest = Map.of(
            "montant", 100000,
            "devise", "XOF",
            "beneficiaire", "FOURNISSEUR-001",
            "motif", "Facture prestation"
        );
        when(paiementService.initierPaiement(any())).thenReturn(Map.of(
            "id", 1L,
            "reference", "PAY-001",
            "statut", "EN_ATTENTE"
        ));

        mockMvc.perform(post(ApiTestConstants.PAIEMENT_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(paiementRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    void shouldExecuterPaiement() throws Exception {
        when(paiementService.executerPaiement(1L)).thenReturn(Map.of(
            "id", 1L,
            "statut", "EXECUTE",
            "dateExecution", "2026-05-02T10:30:00"
        ));

        mockMvc.perform(post(ApiTestConstants.PAIEMENT_BASE + "/1/executer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetPaiementStatus() throws Exception {
        when(paiementService.getStatutPaiement(1L)).thenReturn(Map.of(
            "id", 1L, "statut", "EXECUTE"
        ));

        mockMvc.perform(get(ApiTestConstants.PAIEMENT_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.PAIEMENT_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of())))
            .andExpect(status().isUnauthorized());
    }
}
