package com.microfinance.core_banking.api.controller.tarification;

import com.microfinance.core_banking.entity.Agio;
import com.microfinance.core_banking.service.tarification.AgioService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgioController.class)
class AgioControllerTest extends AbstractControllerTest {

    @MockBean
    private AgioService agioService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldCalculerAgios() throws Exception {
        Agio agio = new Agio();
        agio.setIdAgio(1L);
        agio.setMontantAgio(new BigDecimal("2500.00"));
        agio.setDateDebut(LocalDate.now().minusMonths(1));
        agio.setDateFin(LocalDate.now());

        when(agioService.calculerAgiosCompte("SN001", LocalDate.now().minusMonths(1), LocalDate.now()))
            .thenReturn(agio);

        mockMvc.perform(post(ApiTestConstants.AGIOS_BASE + "/calculer/SN001?debut="
                + LocalDate.now().minusMonths(1) + "&fin=" + LocalDate.now())
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.montantAgio").value(2500.00));
    }

    @Test
    void shouldListerAgios() throws Exception {
        Agio agio = new Agio();
        agio.setIdAgio(1L);
        agio.setMontantAgio(new BigDecimal("2500.00"));

        when(agioService.listerAgiosCompte("SN001")).thenReturn(List.of(agio));

        mockMvc.perform(get(ApiTestConstants.AGIOS_BASE + "/SN001")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.AGIOS_BASE + "/calculer/SN001")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
