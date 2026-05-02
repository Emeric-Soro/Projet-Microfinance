package com.microfinance.core_banking.api.controller.beneficiary;

import com.microfinance.core_banking.entity.Beneficiary;
import com.microfinance.core_banking.service.BeneficiaryService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficiaryController.class)
class BeneficiaryControllerTest extends AbstractControllerTest {

    @MockBean
    private BeneficiaryService beneficiaryService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("CLIENT");
    }

    @Test
    void shouldAjouterBeneficiaire() throws Exception {
        Beneficiary ben = new Beneficiary();
        ben.setId(1L);
        ben.setNom("Beneficiaire Test");
        ben.setNumeroCompte("SN-BEN-001");
        when(beneficiaryService.ajouterBeneficiaire(any(Beneficiary.class))).thenReturn(ben);

        mockMvc.perform(post(ApiTestConstants.BENEFICIAIRES_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(ben)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerBeneficiaires() throws Exception {
        when(beneficiaryService.listerBeneficiaires()).thenReturn(List.of());

        mockMvc.perform(get(ApiTestConstants.BENEFICIAIRES_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldSupprimerBeneficiaire() throws Exception {
        when(beneficiaryService.supprimerBeneficiaire(1L)).thenReturn(true);

        mockMvc.perform(delete(ApiTestConstants.BENEFICIAIRES_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNoContent());
    }
}
