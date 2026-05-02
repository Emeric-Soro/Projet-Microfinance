package com.microfinance.core_banking.api.controller.guarantor;

import com.microfinance.core_banking.entity.Guarantor;
import com.microfinance.core_banking.service.GuarantorService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuarantorController.class)
class GuarantorControllerTest extends AbstractControllerTest {

    @MockBean
    private GuarantorService guarantorService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldAjouterGarant() throws Exception {
        Guarantor garant = new Guarantor();
        garant.setId(1L);
        garant.setNom("Garant Test");
        garant.setMontantEngagement(new BigDecimal("2000000.00"));
        when(guarantorService.ajouterGarant(any(Guarantor.class))).thenReturn(garant);

        mockMvc.perform(post(ApiTestConstants.GARANTS_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(garant)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerGarants() throws Exception {
        when(guarantorService.listerGarants()).thenReturn(List.of());

        mockMvc.perform(get(ApiTestConstants.GARANTS_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldEvaluerGarant() throws Exception {
        Guarantor garant = new Guarantor();
        garant.setId(1L);
        garant.setMontantEngagement(new BigDecimal("2500000.00"));
        when(guarantorService.evaluerGarant(eq(1L), any(BigDecimal.class))).thenReturn(garant);

        mockMvc.perform(put(ApiTestConstants.GARANTS_BASE + "/1/evaluation")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of("montantEngagement", 2500000))))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSupprimerGarant() throws Exception {
        when(guarantorService.supprimerGarant(1L)).thenReturn(true);

        mockMvc.perform(delete(ApiTestConstants.GARANTS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNoContent());
    }
}
