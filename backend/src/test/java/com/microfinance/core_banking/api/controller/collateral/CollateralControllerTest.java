package com.microfinance.core_banking.api.controller.collateral;

import com.microfinance.core_banking.entity.Collateral;
import com.microfinance.core_banking.service.CollateralService;
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

@WebMvcTest(CollateralController.class)
class CollateralControllerTest extends AbstractControllerTest {

    @MockBean
    private CollateralService collateralService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldAjouterCollateral() throws Exception {
        Collateral col = new Collateral();
        col.setId(1L);
        col.setType("BIEN_IMMOBILIER");
        col.setValeurEstimee(new BigDecimal("10000000.00"));
        col.setDescription("Maison a Dakar");
        when(collateralService.ajouterCollateral(any(Collateral.class))).thenReturn(col);

        mockMvc.perform(post(ApiTestConstants.COLLATERALS_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(col)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerCollateraux() throws Exception {
        when(collateralService.listerCollateraux()).thenReturn(List.of());

        mockMvc.perform(get(ApiTestConstants.COLLATERALS_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldEvaluerCollateral() throws Exception {
        Collateral col = new Collateral();
        col.setId(1L);
        col.setValeurEstimee(new BigDecimal("9500000.00"));
        when(collateralService.evaluerCollateral(eq(1L), any(BigDecimal.class))).thenReturn(col);

        mockMvc.perform(put(ApiTestConstants.COLLATERALS_BASE + "/1/evaluation")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(Map.of("valeurEstimee", 9500000))))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSupprimerCollateral() throws Exception {
        when(collateralService.supprimerCollateral(1L)).thenReturn(true);

        mockMvc.perform(delete(ApiTestConstants.COLLATERALS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNoContent());
    }
}
