package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.ConformiteDossier;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConformiteExtensionController.class)
class ConformiteExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private ConformiteExtensionService conformiteService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerDossiersConformite() throws Exception {
        when(conformiteService.listerDossiers()).thenReturn(List.of(Map.of(
            "id", 1, "client", "CLI-001", "statut", "EN_ATTENTE"
        )));

        mockMvc.perform(get(ApiTestConstants.CONFORMITE_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldScreeningClient() throws Exception {
        when(conformiteService.effectuerScreening(1L)).thenReturn(Map.of(
            "clientId", 1L,
            "scoreRisque", 25,
            "niveauRisque", "FAIBLE",
            "alertes", List.of()
        ));

        mockMvc.perform(post(ApiTestConstants.CONFORMITE_BASE + "/screening/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.niveauRisque").value("FAIBLE"));
    }

    @Test
    void shouldDeclarationOperationSuspecte() throws Exception {
        Map<String, String> declaration = Map.of(
            "clientId", "1",
            "typeOperation", "VIREMENT",
            "montant", "5000000",
            "motif", "Transaction inhabituelle"
        );
        when(conformiteService.declarerOperationSuspecte(any())).thenReturn(Map.of(
            "id", 1, "statut", "DECLARE"
        ));

        mockMvc.perform(post(ApiTestConstants.CONFORMITE_BASE + "/declaration")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(declaration)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.CONFORMITE_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
