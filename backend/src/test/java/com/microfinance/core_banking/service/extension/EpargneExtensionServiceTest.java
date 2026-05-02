package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneServiceRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
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
class EpargneExtensionServiceTest {

    @Mock private ProduitEpargneRepository produitEpargneRepository;
    @Mock private DepotATermeRepository depotATermeRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private EpargneExtensionService epargneExtensionService;

    @Test
    void creerProduit_withValidData_shouldSucceed() {
        CreerProduitEpargneServiceRequestDTO dto = new CreerProduitEpargneServiceRequestDTO();
        dto.setCodeProduit("EP-PROD-001");
        dto.setLibelle("Epargne Classique");
        dto.setTauxInteret(new BigDecimal("3.5"));

        when(produitEpargneRepository.save(any(ProduitEpargne.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitEpargne resultat = epargneExtensionService.creerProduit(dto);

        assertNotNull(resultat);
        assertEquals("EP-PROD-001", resultat.getCodeProduit());
        assertEquals("Epargne Classique", resultat.getLibelle());
    }
}
