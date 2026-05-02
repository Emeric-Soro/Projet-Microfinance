package com.microfinance.core_banking.api.controller.tarification;

import com.microfinance.core_banking.entity.tarification.TarificationParametre;
import com.microfinance.core_banking.service.tarification.TarificationParametreService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarificationParametreController.class)
class TarificationParametreControllerTest extends AbstractControllerTest {

    @MockBean
    private TarificationParametreService tarificationService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerParametres() throws Exception {
        TarificationParametre param = new TarificationParametre();
        param.setCodeParametre("FRAIS_DOSSIER");
        param.setValeur(new BigDecimal("5000"));
        param.setActif(true);
        when(tarificationService.listerParametres()).thenReturn(List.of(param));

        mockMvc.perform(get(ApiTestConstants.TARIFICATION_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCreerParametre() throws Exception {
        TarificationParametre param = new TarificationParametre();
        param.setCodeParametre("FRAIS_DOSSIER");
        param.setValeur(new BigDecimal("5000"));
        when(tarificationService.creerParametre(any(TarificationParametre.class))).thenReturn(param);

        mockMvc.perform(post(ApiTestConstants.TARIFICATION_BASE)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(param)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldGetParametreByCode() throws Exception {
        TarificationParametre param = new TarificationParametre();
        param.setCodeParametre("FRAIS_DOSSIER");
        param.setValeur(new BigDecimal("5000"));
        when(tarificationService.rechercherParCode("FRAIS_DOSSIER")).thenReturn(Optional.of(param));

        mockMvc.perform(get(ApiTestConstants.TARIFICATION_BASE + "/FRAIS_DOSSIER")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }
}
