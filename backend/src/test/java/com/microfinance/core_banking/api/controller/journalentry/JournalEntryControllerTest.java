package com.microfinance.core_banking.api.controller.journalentry;

import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.service.extension.JournalEntryService;
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

@WebMvcTest(JournalEntryController.class)
class JournalEntryControllerTest extends AbstractControllerTest {

    @MockBean
    private JournalEntryService journalEntryService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldCreerEcriture() throws Exception {
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setIdEcritureComptable(1L);
        ecriture.setReferencePiece("EC-001");
        ecriture.setDateComptable(LocalDate.now());
        when(journalEntryService.creerEcriture(any(EcritureComptable.class))).thenReturn(ecriture);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/journal-entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(ecriture)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldListerEcritures() throws Exception {
        when(journalEntryService.listerEcritures()).thenReturn(List.of());

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/journal-entries")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetEcritureById() throws Exception {
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setIdEcritureComptable(1L);
        ecriture.setReferencePiece("EC-001");
        when(journalEntryService.rechercherParId(1L)).thenReturn(Optional.of(ecriture));

        mockMvc.perform(get(ApiTestConstants.COMPTABILITE_BASE + "/journal-entries/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldValiderEcriture() throws Exception {
        EcritureComptable ecriture = new EcritureComptable();
        ecriture.setIdEcritureComptable(1L);
        ecriture.setValide(true);
        when(journalEntryService.validerEcriture(1L)).thenReturn(ecriture);

        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/journal-entries/1/valider")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valide").value(true));
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post(ApiTestConstants.COMPTABILITE_BASE + "/journal-entries")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
