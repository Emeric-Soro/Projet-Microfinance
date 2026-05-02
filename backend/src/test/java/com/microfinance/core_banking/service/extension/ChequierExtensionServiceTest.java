package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CommanderChequierRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.extension.ChequierRepository;
import com.microfinance.core_banking.repository.extension.RemiseChequeRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChequierExtensionServiceTest {

    @Mock private ChequierRepository chequierRepository;
    @Mock private RemiseChequeRepository remiseChequeRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ChequierExtensionService chequierExtensionService;

    @Test
    void commander_withValidData_shouldSucceed() {
        Compte compte = new Compte();
        compte.setIdCompte(1L);
        compte.setNumCompte("CPT-001");

        CommanderChequierRequestDTO dto = new CommanderChequierRequestDTO(1L, 50, "1");

        when(compteRepository.findById(1L)).thenReturn(Optional.of(compte));
        when(chequierRepository.save(any(Chequier.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Chequier resultat = chequierExtensionService.commander(dto);

        assertNotNull(resultat);
        assertEquals("COMMANDE", resultat.getStatut());
    }

    @Test
    void commander_withUnknownCompte_shouldThrow() {
        CommanderChequierRequestDTO dto = new CommanderChequierRequestDTO(999L, 50, "999");
        when(compteRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> chequierExtensionService.commander(dto));
    }
}
