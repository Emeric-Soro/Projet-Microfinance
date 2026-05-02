package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerAgentRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentBankingExtensionServiceTest {

    @Mock private AgentRepository agentRepository;
    @Mock private PortefeuilleAgentRepository portefeuilleAgentRepository;
    @Mock private TransactionAgentRepository transactionAgentRepository;
    @Mock private CommissionAgentRepository commissionAgentRepository;
    @Mock private AgenceRepository agenceRepository;

    @InjectMocks
    private AgentBankingExtensionService agentBankingExtensionService;

    @Test
    void creerAgent_withValidData_shouldSucceed() {
        CreerAgentRequestDTO dto = new CreerAgentRequestDTO();
        dto.setNomAgent("Agent Test");
        dto.setTelephone("0102030405");
        dto.setIdAgenceRattachement(1L);

        Agence agence = new Agence();
        agence.setIdAgence(1L);
        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence));
        when(agentRepository.save(any(Agent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Agent resultat = agentBankingExtensionService.creerAgent(dto);

        assertNotNull(resultat);
        assertEquals("Agent Test", resultat.getNomAgent());
        assertEquals("0102030405", resultat.getTelephone());
    }

    @Test
    void creerAgent_withUnknownAgence_shouldThrow() {
        CreerAgentRequestDTO dto = new CreerAgentRequestDTO();
        dto.setIdAgenceRattachement(999L);
        dto.setNomAgent("Agent Test");
        dto.setTelephone("0102030405");

        when(agenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> agentBankingExtensionService.creerAgent(dto));
    }
}
