package com.microfinance.core_banking.api.controller.compte;

import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.service.compte.CarteVisaService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarteVisaController.class)
class CarteVisaControllerTest extends AbstractControllerTest {

    @MockBean
    private CarteVisaService carteVisaService;

    private String adminToken;
    private CarteVisa sampleCarte;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        sampleCarte = new CarteVisa();
        sampleCarte.setIdCarte(1L);
        sampleCarte.setNumeroCarte("4532015112830366");
        sampleCarte.setDateExpiration(LocalDate.now().plusYears(3));
        sampleCarte.setStatut("ACTIVE");
    }

    @Test
    void shouldCreateCarte() throws Exception {
        when(carteVisaService.creerCarte(any(CarteVisa.class))).thenReturn(sampleCarte);

        mockMvc.perform(post(ApiTestConstants.CARTES_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(sampleCarte)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetCarteById() throws Exception {
        when(carteVisaService.rechercherParId(1L)).thenReturn(Optional.of(sampleCarte));

        mockMvc.perform(get(ApiTestConstants.CARTES_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroCarte").value("4532015112830366"));
    }

    @Test
    void shouldBlockCarte() throws Exception {
        sampleCarte.setStatut("BLOQUEE");
        when(carteVisaService.bloquerCarte(1L)).thenReturn(sampleCarte);

        mockMvc.perform(post(ApiTestConstants.CARTES_BASE + "/1/bloquer")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statut").value("BLOQUEE"));
    }

    @Test
    void shouldGetAllCartes() throws Exception {
        when(carteVisaService.listerToutes()).thenReturn(List.of(sampleCarte));

        mockMvc.perform(get(ApiTestConstants.CARTES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }
}
