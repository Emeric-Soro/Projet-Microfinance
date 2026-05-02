package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.MonetiqueService;
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

@WebMvcTest(MonetiqueController.class)
class MonetiqueControllerTest extends AbstractControllerTest {

    @MockBean
    private MonetiqueService monetiqueService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldProcessPayment() throws Exception {
        Map<String, Object> paymentRequest = Map.of(
            "numeroCarte", "4532015112830366",
            "montant", 25000,
            "devise", "XOF",
            "codeSecurite", "123"
        );
        when(monetiqueService.traiterPaiement(any())).thenReturn(Map.of(
            "statut", "APPROUVE",
            "reference", "PAY-CB-001",
            "montant", 25000
        ));

        mockMvc.perform(post(ApiTestConstants.MONETIQUE_BASE + "/paiement")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(paymentRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("APPROUVE"));
    }

    @Test
    void shouldGetTransactionsMonetiques() throws Exception {
        when(monetiqueService.listerTransactions()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.MONETIQUE_BASE + "/transactions")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.MONETIQUE_BASE + "/paiement")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
