package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.service.extension.ValidationExtensionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValidationExtensionController.class)
class ValidationExtensionControllerTest extends AbstractControllerTest {

    @MockBean
    private ValidationExtensionService validationService;

    private String adminToken;
    private ActionEnAttente sampleAction;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("SUPERVISEUR");
        sampleAction = TestDataFactory.createSampleActionEnAttente();
    }

    @Test
    void shouldCreerAction() throws Exception {
        when(validationService.creerActionEnAttente(
            anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(sampleAction);

        Map<String, String> actionRequest = Map.of(
            "typeAction", "CREATION",
            "ressource", "COMPTE",
            "referenceRessource", "REF-001",
            "nouvelleValeur", "{\"type\":\"EPARGNE\"}",
            "commentaire", "Creation compte"
        );

        mockMvc.perform(post(ApiTestConstants.VALIDATIONS_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(actionRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerActionsEnAttente() throws Exception {
        when(validationService.listerActionsEnAttente()).thenReturn(List.of(sampleAction));

        mockMvc.perform(get(ApiTestConstants.VALIDATIONS_BASE + "/en-attente")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].typeAction").value("CREATION"));
    }

    @Test
    void shouldListerToutesActions() throws Exception {
        when(validationService.listerToutesActions()).thenReturn(List.of(sampleAction));

        mockMvc.perform(get(ApiTestConstants.VALIDATIONS_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValiderAction() throws Exception {
        sampleAction.setStatut("APPROUVE");
        when(validationService.validerAction(eq(1L), anyString())).thenReturn(sampleAction);

        Map<String, String> validationRequest = Map.of("commentaire", "Approuve par superviseur");
        mockMvc.perform(post(ApiTestConstants.VALIDATIONS_BASE + "/1/valider")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(validationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("APPROUVE"));
    }

    @Test
    void shouldRejeterAction() throws Exception {
        sampleAction.setStatut("REJETE");
        when(validationService.rejeterAction(eq(1L), anyString())).thenReturn(sampleAction);

        Map<String, String> rejectionRequest = Map.of("commentaire", "Documentation incomplete");
        mockMvc.perform(post(ApiTestConstants.VALIDATIONS_BASE + "/1/rejeter")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rejectionRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("REJETE"));
    }

    @Test
    void shouldGetActionById() throws Exception {
        when(validationService.rechercherActionParId(1L)).thenReturn(Optional.of(sampleAction));

        mockMvc.perform(get(ApiTestConstants.VALIDATIONS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.VALIDATIONS_BASE + "/en-attente")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
