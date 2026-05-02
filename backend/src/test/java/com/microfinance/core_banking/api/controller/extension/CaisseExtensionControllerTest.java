package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.extension.CaisseExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CaisseExtensionController.class)
class CaisseExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private CaisseExtensionService caisseService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldGetAllCaisses() throws Exception {
        Caisse caisse = TestDataFactory.createSampleCaisse();
        when(caisseService.listerCaisses()).thenReturn(List.of(caisse));

        mockMvc.perform(get(ApiTestConstants.CAISSES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeCaisse").value("CAI-001"));
    }

    @Test
    void shouldCreateCaisse() throws Exception {
        Caisse caisse = TestDataFactory.createSampleCaisse();
        when(caisseService.creerCaisse(any(Caisse.class))).thenReturn(caisse);

        mockMvc.perform(post(ApiTestConstants.CAISSES_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(caisse)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldOuvertureSession() throws Exception {
        SessionCaisse session = TestDataFactory.createSampleSessionCaisse();
        when(caisseService.ouvrirSession(any(SessionCaisse.class))).thenReturn(session);

        mockMvc.perform(post(ApiTestConstants.CAISSES_BASE + "/sessions/ouverture")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(session)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldFermetureSession() throws Exception {
        SessionCaisse session = TestDataFactory.createSampleSessionCaisse();
        session.setStatut("FERMEE");
        when(caisseService.fermerSession(eq(1L), any(BigDecimal.class))).thenReturn(session);

        Map<String, Object> fermetureRequest = Map.of("soldeFermeture", 550000);
        mockMvc.perform(post(ApiTestConstants.CAISSES_BASE + "/sessions/1/fermeture")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(fermetureRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("FERMEE"));
    }

    @Test
    void shouldGetBilletage() throws Exception {
        BilletageCaisse billetage = TestDataFactory.createSampleBilletage();
        when(caisseService.listerBilletage()).thenReturn(List.of(billetage));

        mockMvc.perform(get(ApiTestConstants.CAISSES_BASE + "/billetage")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldArreterCaisse() throws Exception {
        Caisse caisse = TestDataFactory.createSampleCaisse();
        when(caisseService.arreterCaisse(1L)).thenReturn(caisse);

        mockMvc.perform(post(ApiTestConstants.CAISSES_BASE + "/1/arrete")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetSessionCaisse() throws Exception {
        SessionCaisse session = TestDataFactory.createSampleSessionCaisse();
        when(caisseService.rechercherSessionParId(1L)).thenReturn(Optional.of(session));

        mockMvc.perform(get(ApiTestConstants.CAISSES_BASE + "/sessions/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetCaisseByCode() throws Exception {
        Caisse caisse = TestDataFactory.createSampleCaisse();
        when(caisseService.rechercherParCode("CAI-001")).thenReturn(Optional.of(caisse));

        mockMvc.perform(get(ApiTestConstants.CAISSES_BASE + "/code/CAI-001")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.CAISSES_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
