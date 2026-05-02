package com.microfinance.core_banking.api.controller.client;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.client.ClientService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest extends AbstractControllerTest {

    @MockBean
    private ClientService clientService;

    private String adminToken;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        sampleClient = TestDataFactory.createSampleClient();
    }

    @Test
    void shouldCreateClient() throws Exception {
        when(clientService.creerClient(any(Client.class))).thenReturn(sampleClient);

        mockMvc.perform(post(ApiTestConstants.CLIENTS_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleClient)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.codeClient").value(sampleClient.getCodeClient()));
    }

    @Test
    void shouldGetClientById() throws Exception {
        when(clientService.rechercherClientParId(1L)).thenReturn(Optional.of(sampleClient));

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    void shouldReturn404WhenClientNotFound() throws Exception {
        when(clientService.rechercherClientParId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE + "/999")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllClients() throws Exception {
        when(clientService.listerTousClients()).thenReturn(List.of(sampleClient));

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }

    @Test
    void shouldUpdateClient() throws Exception {
        sampleClient.setNom("DupontModifie");
        when(clientService.modifierClient(eq(1L), any(Client.class))).thenReturn(sampleClient);

        mockMvc.perform(put(ApiTestConstants.CLIENTS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleClient)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("DupontModifie"));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        when(clientService.supprimerClient(1L)).thenReturn(true);

        mockMvc.perform(delete(ApiTestConstants.CLIENTS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldSubmitKyc() throws Exception {
        when(clientService.soumettreDossierKyc(eq(1L))).thenReturn(sampleClient);

        mockMvc.perform(post(ApiTestConstants.CLIENTS_BASE + "/1/kyc/soumettre")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetClientByCode() throws Exception {
        when(clientService.rechercherClientParCode("CLI-TEST")).thenReturn(Optional.of(sampleClient));

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE + "/code/CLI-TEST")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetClientComptes() throws Exception {
        Compte compte = TestDataFactory.createSampleCompte();
        when(clientService.listerComptesClient(1L)).thenReturn(List.of(compte));

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE + "/1/comptes")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].numCompte").value(compte.getNumCompte()));
    }

    @Test
    void shouldSearchClients() throws Exception {
        when(clientService.rechercherClients("Dupont", null, null))
            .thenReturn(List.of(sampleClient));

        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE + "/search?nom=Dupont")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.CLIENTS_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
