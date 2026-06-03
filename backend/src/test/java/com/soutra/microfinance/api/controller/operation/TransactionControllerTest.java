package com.soutra.microfinance.api.controller.operation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutra.microfinance.config.JwtAuthenticationFilter;
import com.soutra.microfinance.config.JwtService;
import com.soutra.microfinance.config.JwtTokenBlacklistService;
import com.soutra.microfinance.config.PublicApiRateLimitProperties;
import com.soutra.microfinance.config.PublicApiRateLimitingFilter;
import com.soutra.microfinance.dto.request.operation.TransactionSimpleRequestDTO;
import com.soutra.microfinance.dto.request.operation.ValidationTransactionRequestDTO;
import com.soutra.microfinance.dto.request.operation.VirementRequestDTO;
import com.soutra.microfinance.entity.*;
import com.soutra.microfinance.dto.response.operation.RecuTransactionResponseDTO;
import com.soutra.microfinance.mapper.OperationMapper;
import com.soutra.microfinance.service.operation.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableConfigurationProperties(PublicApiRateLimitProperties.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private OperationMapper operationMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenBlacklistService jwtTokenBlacklistService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private PublicApiRateLimitingFilter publicApiRateLimitingFilter;

    private UsernamePasswordAuthenticationToken getTestAuthentication() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdUser(1L);
        utilisateur.setLogin("test.user");
        
        return new UsernamePasswordAuthenticationToken(
            utilisateur,
            null,
            List.of(
                new SimpleGrantedAuthority("ADMIN"),
                new SimpleGrantedAuthority("GUICHETIER"),
                new SimpleGrantedAuthority("SUPERVISEUR")
            )
        );
    }

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(getTestAuthentication());
    }

    @Test
    void shouldCreateDepotSuccessfully() throws Exception {
        TransactionSimpleRequestDTO request = new TransactionSimpleRequestDTO(
                "CPT-001", new BigDecimal("50000"), 1L, null
        );
        Transaction transaction = buildTransaction("TX-001", StatutOperation.EXECUTEE);

        when(transactionService.faireDepot(anyString(), any(BigDecimal.class), anyLong()))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/v1/transactions/depot")
                        .with(authentication(getTestAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceUnique").value("TX-001"));
    }

    @Test
    void shouldReturnPendingTransactionForLargeDepot() throws Exception {
        TransactionSimpleRequestDTO request = new TransactionSimpleRequestDTO(
                "CPT-001", new BigDecimal("1000000"), 1L, null
        );
        Transaction transaction = buildTransaction("TX-002", StatutOperation.EN_ATTENTE);
        transaction.setValidationSuperviseurRequise(true);

        when(transactionService.faireDepot(anyString(), any(BigDecimal.class), anyLong()))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/v1/transactions/depot")
                        .with(authentication(getTestAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.statutOperation").value("EN_ATTENTE"));
    }

    @Test
    void shouldListPendingTransactions() throws Exception {
        Page<Transaction> page = new PageImpl<>(List.of(
                buildTransaction("TX-001", StatutOperation.EN_ATTENTE)
        ));

        when(transactionService.listerEnAttente(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions/en-attente")
                        .with(authentication(getTestAuthentication()))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void shouldApproveTransaction() throws Exception {
        ValidationTransactionRequestDTO request = new ValidationTransactionRequestDTO(2L, null);
        Transaction transaction = buildTransaction("TX-001", StatutOperation.EXECUTEE);
        RecuTransactionResponseDTO responseDTO = new RecuTransactionResponseDTO();
        responseDTO.setReferenceUnique("TX-001");
        responseDTO.setStatutOperation("EXECUTEE");

        when(transactionService.approuverTransaction(anyString(), anyLong()))
                .thenReturn(transaction);
        when(operationMapper.toRecuResponseDTO(transaction)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/transactions/TX-001/approbation")
                        .with(authentication(getTestAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutOperation").value("EXECUTEE"));
    }

    @Test
    void shouldRejectTransaction() throws Exception {
        Transaction transaction = buildTransaction("TX-001", StatutOperation.REJETEE);
        transaction.setMotifRejet("Suspicion de fraude");
        RecuTransactionResponseDTO responseDTO = new RecuTransactionResponseDTO();
        responseDTO.setReferenceUnique("TX-001");
        responseDTO.setStatutOperation("REJETEE");

        when(transactionService.rejeterTransaction(anyString(), anyLong(), anyString()))
                .thenReturn(transaction);
        when(operationMapper.toRecuResponseDTO(any(Transaction.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/transactions/TX-001/rejet")
                        .with(authentication(getTestAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idSuperviseur\": 2, \"motif\": \"Suspicion de fraude\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutOperation").value("REJETEE"));
    }

    @Test
    void shouldReturn400WhenMontantIsNegative() throws Exception {
        TransactionSimpleRequestDTO request = new TransactionSimpleRequestDTO(
                "CPT-001", new BigDecimal("-100"), 1L, null
        );

        when(transactionService.faireDepot(anyString(), any(BigDecimal.class), anyLong()))
                .thenThrow(new IllegalArgumentException("Le montant doit etre strictement positif"));

        mockMvc.perform(post("/api/v1/transactions/depot")
                        .with(authentication(getTestAuthentication()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private Transaction buildTransaction(String ref, StatutOperation statut) {
        Transaction tx = new Transaction();
        tx.setIdTransaction(1L);
        tx.setReferenceUnique(ref);
        tx.setMontantGlobal(new BigDecimal("50000"));
        tx.setFrais(BigDecimal.ZERO);
        tx.setStatutOperation(statut);
        tx.setDateHeureTransaction(LocalDateTime.now());
        tx.setValidationSuperviseurRequise(false);
        TypeTransaction type = new TypeTransaction();
        type.setCodeTypeTransaction("DEPOT");
        type.setLibelle("Depot");
        tx.setTypeTransaction(type);
        return tx;
    }
}
