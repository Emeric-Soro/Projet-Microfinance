package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerCaisseServiceRequestDTO;
import com.microfinance.core_banking.dto.request.extension.OuvrirSessionServiceRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.UtilisateurRepository;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
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
class TresorerieServiceTest {

    @Mock private CaisseRepository caisseRepository;
    @Mock private CoffreRepository coffreRepository;
    @Mock private MouvementCoffreRepository mouvementCoffreRepository;
    @Mock private ApprovisionnementCaisseRepository approvisionnementCaisseRepository;
    @Mock private DelestageCaisseRepository delestageCaisseRepository;
    @Mock private SessionCaisseRepository sessionCaisseRepository;
    @Mock private AgenceRepository agenceRepository;
    @Mock private GuichetRepository guichetRepository;
    @Mock private UtilisateurRepository utilisateurRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Mock private ComptabiliteExtensionService comptabiliteExtensionService;

    @InjectMocks
    private TresorerieService tresorerieService;

    @Test
    void creerCaisse_withValidData_shouldSucceed() {
        Agence agence = new Agence();
        agence.setIdAgence(1L);

        CreerCaisseServiceRequestDTO dto = new CreerCaisseServiceRequestDTO();
        dto.setIdAgence("1");
        dto.setCodeCaisse("CAISSE001");
        dto.setLibelle("Caisse Principale");
        dto.setSoldeTheorique(BigDecimal.ZERO);

        when(agenceRepository.findById(1L)).thenReturn(Optional.of(agence));
        when(caisseRepository.save(any(Caisse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Caisse resultat = tresorerieService.creerCaisse(dto);

        assertNotNull(resultat);
        assertEquals("CAISSE001", resultat.getCodeCaisse());
        assertEquals("Caisse Principale", resultat.getLibelle());
        assertEquals(agence, resultat.getAgence());
    }

    @Test
    void creerCaisse_withUnknownAgence_shouldThrow() {
        CreerCaisseServiceRequestDTO dto = new CreerCaisseServiceRequestDTO();
        dto.setIdAgence("999");
        dto.setCodeCaisse("CAISSE999");

        when(agenceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> tresorerieService.creerCaisse(dto));
    }
}
