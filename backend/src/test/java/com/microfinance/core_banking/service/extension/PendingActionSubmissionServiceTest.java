package com.microfinance.core_banking.service.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.core_banking.entity.ActionEnAttente;
import com.microfinance.core_banking.repository.extension.ActionEnAttenteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingActionSubmissionServiceTest {

    @Mock private ActionEnAttenteRepository actionEnAttenteRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private PendingActionSubmissionService pendingActionSubmissionService;

    @Test
    void submit_withBasicParams_shouldSucceed() {
        when(actionEnAttenteRepository.save(any(ActionEnAttente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActionEnAttente resultat = pendingActionSubmissionService.submit(
                "TEST_ACTION", "1", "{}", "{}", "Test submission"
        );

        assertNotNull(resultat);
        assertEquals("TEST_ACTION", resultat.getTypeAction());
        assertEquals("EN_ATTENTE", resultat.getStatut());
    }
}
