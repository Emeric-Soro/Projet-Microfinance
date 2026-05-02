package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.SystemAuditLog;
import com.microfinance.core_banking.repository.extension.SystemAuditLogRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemAuditLogServiceTest {

    @Mock private SystemAuditLogRepository systemAuditLogRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private SystemAuditLogService systemAuditLogService;

    @Test
    void getLogsByUser_withValidUser_shouldReturnList() {
        SystemAuditLog log = new SystemAuditLog();
        log.setAction("LOGIN");
        log.setUserName("testuser");

        Page<SystemAuditLog> page = new PageImpl<>(List.of(log));
        when(systemAuditLogRepository.findByUserName("testuser", Pageable.unpaged()))
                .thenReturn(page);

        Page<SystemAuditLog> resultats = systemAuditLogService.getAuditLogsByUser("testuser", Pageable.unpaged());

        assertNotNull(resultats);
        assertEquals(1, resultats.getContent().size());
        assertEquals("LOGIN", resultats.getContent().get(0).getAction());
    }

    @Test
    void getLogsByUser_withUnknownUser_shouldReturnEmpty() {
        when(systemAuditLogRepository.findByUserName("unknown", Pageable.unpaged()))
                .thenReturn(Page.empty());

        Page<SystemAuditLog> resultats = systemAuditLogService.getAuditLogsByUser("unknown", Pageable.unpaged());

        assertNotNull(resultats);
        assertTrue(resultats.isEmpty());
    }
}
