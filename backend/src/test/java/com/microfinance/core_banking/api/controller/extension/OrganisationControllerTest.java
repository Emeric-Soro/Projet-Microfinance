package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.entity.Agence;
import com.microfinance.core_banking.service.extension.OrganisationService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganisationController.class)
class OrganisationControllerTest extends AbstractControllerTest {

    @MockBean
    private OrganisationService organisationService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerAgences() throws Exception {
        Agence agence = new Agence();
        agence.setIdAgence(1L);
        agence.setCodeAgence("AG-DKR-001");
        agence.setLibelleAgence("Agence Dakar Plateau");
        when(organisationService.listerAgences()).thenReturn(List.of(agence));

        mockMvc.perform(get(ApiTestConstants.ORGANISATIONS_BASE + "/agences")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].codeAgence").value("AG-DKR-001"));
    }

    @Test
    void shouldCreerAgence() throws Exception {
        Agence agence = new Agence();
        agence.setCodeAgence("AG-NEW-001");
        agence.setLibelleAgence("Nouvelle Agence");

        when(organisationService.creerAgence(any(Agence.class))).thenReturn(agence);

        mockMvc.perform(post(ApiTestConstants.ORGANISATIONS_BASE + "/agences")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(agence)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetAgenceById() throws Exception {
        Agence agence = new Agence();
        agence.setIdAgence(1L);
        agence.setCodeAgence("AG-DKR-001");

        when(organisationService.rechercherAgenceParId(1L)).thenReturn(Optional.of(agence));

        mockMvc.perform(get(ApiTestConstants.ORGANISATIONS_BASE + "/agences/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldModifierAgence() throws Exception {
        Agence agence = new Agence();
        agence.setIdAgence(1L);
        agence.setLibelleAgence("Agence Modifiee");

        when(organisationService.modifierAgence(eq(1L), any(Agence.class))).thenReturn(agence);

        mockMvc.perform(put(ApiTestConstants.ORGANISATIONS_BASE + "/agences/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(agence)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.ORGANISATIONS_BASE + "/agences")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
