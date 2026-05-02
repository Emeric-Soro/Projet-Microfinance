package com.microfinance.core_banking.api.controller.extension;

import com.microfinance.core_banking.service.config.SystemAuditLogService;
import com.microfinance.core_banking.support.AbstractControllerTest;
import com.microfinance.core_banking.support.ApiTestConstants;
import com.microfinance.core_banking.support.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemAuditLogController.class)
class SystemAuditLogControllerTest extends AbstractControllerTest {

    @MockBean
    private SystemAuditLogService auditService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = JwtTokenProvider.generateTokenWithRole("ADMIN");
    }

    @Test
    void shouldListerLogs() throws Exception {
        when(auditService.listerLogs()).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.AUDIT_BASE)
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldListerLogsParPeriode() throws Exception {
        when(auditService.rechercherLogsParPeriode(
            java.time.LocalDateTime.now().minusDays(7),
            java.time.LocalDateTime.now())
        ).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/audit?debut=" + LocalDateTime.now().minusDays(7) + "&fin=" + LocalDateTime.now())
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldGetLogsByUser() throws Exception {
        when(auditService.rechercherLogsParUtilisateur("admin")).thenReturn(java.util.List.of());

        mockMvc.perform(get(ApiTestConstants.AUDIT_BASE + "/utilisateur/admin")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get(ApiTestConstants.AUDIT_BASE)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
