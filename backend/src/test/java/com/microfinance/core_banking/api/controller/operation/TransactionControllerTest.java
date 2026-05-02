package com.microfinance.core_banking.api.controller.operation;

import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutOperation;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import com.microfinance.core_banking.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest extends AbstractControllerTest {

    @MockBean
    private TransactionService transactionService;

    private String adminToken;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
        sampleTransaction = TestDataFactory.createSampleTransaction();
    }

    @Test
    void shouldCreateDepot() throws Exception {
        Map<String, Object> depotRequest = Map.of(
            "compteDestination", "SN001",
            "montant", 100000,
            "reference", "DEP-001"
        );
        when(transactionService.effectuerDepot(anyString(), any(BigDecimal.class), anyString()))
            .thenReturn(sampleTransaction);

        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/depot")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(depotRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateRetrait() throws Exception {
        Map<String, Object> retraitRequest = Map.of(
            "compteSource", "SN001",
            "montant", 50000,
            "reference", "RET-001"
        );
        when(transactionService.effectuerRetrait(anyString(), any(BigDecimal.class), anyString()))
            .thenReturn(sampleTransaction);

        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/retrait")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(retraitRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateVirement() throws Exception {
        Map<String, Object> virementRequest = Map.of(
            "compteSource", "SN001",
            "compteDestination", "SN002",
            "montant", 75000,
            "reference", "VIR-001"
        );
        when(transactionService.effectuerVirement(anyString(), anyString(), any(BigDecimal.class), anyString()))
            .thenReturn(sampleTransaction);

        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/virement")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(virementRequest)))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldApproveTransaction() throws Exception {
        when(transactionService.approuverTransaction(eq(1L), anyString()))
            .thenReturn(sampleTransaction);

        Map<String, String> approvalRequest = Map.of("commentaire", "Approuve");
        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/1/approuver")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(approvalRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldRejectTransaction() throws Exception {
        when(transactionService.rejeterTransaction(eq(1L), anyString()))
            .thenReturn(sampleTransaction);

        Map<String, String> rejectRequest = Map.of("motif", "Fonds insuffisants");
        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/1/rejeter")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rejectRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldCancelTransaction() throws Exception {
        when(transactionService.annulerTransaction(eq(1L), anyString()))
            .thenReturn(sampleTransaction);

        Map<String, String> cancelRequest = Map.of("motif", "Annulation client");
        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/1/annuler")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cancelRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldExtournerTransaction() throws Exception {
        when(transactionService.extournerTransaction(eq(1L), anyString()))
            .thenReturn(sampleTransaction);

        Map<String, String> extourneRequest = Map.of("motif", "Erreur de saisie");
        mockMvc.perform(post(ApiTestConstants.TRANSACTIONS_BASE + "/1/extourner")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(extourneRequest)))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetTransactionById() throws Exception {
        when(transactionService.rechercherParId(1L)).thenReturn(Optional.of(sampleTransaction));

        mockMvc.perform(get(ApiTestConstants.TRANSACTIONS_BASE + "/1")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.referenceUnique").value(sampleTransaction.getReferenceUnique()));
    }

    @Test
    void shouldGetHistorique() throws Exception {
        when(transactionService.listerTransactions())
            .thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get(ApiTestConstants.TRANSACTIONS_BASE + "/historique")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].referenceUnique").value(sampleTransaction.getReferenceUnique()));
    }

    @Test
    void shouldGetTransactionsByCompte() throws Exception {
        when(transactionService.listerTransactionsParCompte("SN001"))
            .thenReturn(List.of(sampleTransaction));

        mockMvc.perform(get(ApiTestConstants.TRANSACTIONS_BASE + "/compte/SN001")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetTransactionsByDate() throws Exception {
        Page<Transaction> page = new PageImpl<>(List.of(sampleTransaction));
        when(transactionService.listerTransactionsParDate(any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
            .thenReturn(page);

        mockMvc.perform(get(ApiTestConstants.TRANSACTIONS_BASE + "/date?debut=2026-01-01T00:00:00&fin=2026-12-31T23:59:59")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.TRANSACTIONS_BASE + "/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
