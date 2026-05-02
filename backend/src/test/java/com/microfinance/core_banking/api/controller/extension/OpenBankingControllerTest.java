package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.extension.OpenBankingService;
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

@WebMvcTest(OpenBankingController.class)
class OpenBankingControllerTest extends AbstractControllerTest {

    @MockBean
    private OpenBankingService openBankingService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldRegisterThirdParty() throws Exception {
        Map<String, String> registerRequest = Map.of(
            "nom", "Fintech Partner",
            "urlCallback", "https://partner.com/webhook",
            "typeAcces", "READ_ONLY"
        );
        when(openBankingService.enregistrerTiers(any())).thenReturn(Map.of(
            "clientId", "CLIENT-001",
            "clientSecret", "sec-12345",
            "statut", "ACTIF"
        ));

        mockMvc.perform(post(ApiTestConstants.OPENBANKING_BASE + "/tiers")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(registerRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetConsentements() throws Exception {
        when(openBankingService.listerConsentements()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.OPENBANKING_BASE + "/consentements")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRevokeConsentement() throws Exception {
        when(openBankingService.revoquerConsentement("CONS-001")).thenReturn(Map.of(
            "statut", "REVOQUE"
        ));

        mockMvc.perform(post(ApiTestConstants.OPENBANKING_BASE + "/consentements/CONS-001/revoquer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.OPENBANKING_BASE + "/tiers")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
