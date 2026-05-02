package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerRisqueServiceRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.*;
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
class RisqueExtensionServiceTest {

    @Mock private RisqueRepository risqueRepository;
    @Mock private IncidentOperationnelRepository incidentRepository;
    @Mock private StressTestRepository stressTestRepository;
    @Mock private ResultatStressTestRepository resultatStressTestRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private RisqueExtensionService risqueExtensionService;

    @Test
    void creerRisque_withValidData_shouldSucceed() {
        CreerRisqueServiceRequestDTO dto = new CreerRisqueServiceRequestDTO();
        dto.setCodeRisque("RISK-001");
        dto.setLibelle("Risque de credit");
        dto.setCategorie("CREDIT");
        dto.setNiveau("ELEVE");

        when(risqueRepository.save(any(Risque.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Risque resultat = risqueExtensionService.creerRisque(dto);

        assertNotNull(resultat);
        assertEquals("RISK-001", resultat.getCodeRisque());
        assertEquals("Risque de credit", resultat.getLibelle());
        assertEquals("CREDIT", resultat.getCategorie());
    }
}
