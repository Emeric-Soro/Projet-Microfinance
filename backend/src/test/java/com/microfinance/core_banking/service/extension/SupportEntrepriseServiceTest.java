package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerBudgetServiceRequestDTO;
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
class SupportEntrepriseServiceTest {

    @Mock private BudgetExploitationRepository budgetRepository;
    @Mock private LigneBudgetRepository ligneBudgetRepository;
    @Mock private FournisseurRepository fournisseurRepository;
    @Mock private CommandeAchatRepository commandeAchatRepository;
    @Mock private ImmobilisationRepository immobilisationRepository;
    @Mock private BulletinPaieRepository bulletinPaieRepository;
    @Mock private AgenceRepository agenceRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private SupportEntrepriseService supportEntrepriseService;

    @Test
    void creerBudget_withValidData_shouldSucceed() {
        CreerBudgetServiceRequestDTO dto = new CreerBudgetServiceRequestDTO();
        dto.setIdAgence("1");
        dto.setAnnee("2026");
        dto.setMontantTotal(new BigDecimal("10000000"));

        Agence agence = new Agence();
        agence.setIdAgence(1L);

        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence));
        when(budgetRepository.save(any(BudgetExploitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BudgetExploitation resultat = supportEntrepriseService.creerBudget(dto);

        assertNotNull(resultat);
        assertEquals(Integer.valueOf(2026), resultat.getAnnee());
        assertEquals(0, new BigDecimal("10000000").compareTo(resultat.getMontantTotal()));
    }

    @Test
    void creerBudget_withUnknownAgence_shouldThrow() {
        CreerBudgetServiceRequestDTO dto = new CreerBudgetServiceRequestDTO();
        dto.setIdAgence("999");
        dto.setAnnee("2026");
        dto.setMontantTotal(BigDecimal.ZERO);

        when(agenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> supportEntrepriseService.creerBudget(dto));
    }
}
