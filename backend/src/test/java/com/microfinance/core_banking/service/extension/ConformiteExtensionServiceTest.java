package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CreerAlerteServiceRequestDTO;
import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.extension.*;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.operation.TransactionRepository;
import com.microfinance.core_banking.repository.tarification.AgioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConformiteExtensionServiceTest {

    @Mock private AlerteConformiteRepository alerteConformiteRepository;
    @Mock private RapportReglementaireRepository rapportReglementaireRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private CreditRepository creditRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private CaisseRepository caisseRepository;
    @Mock private CoffreRepository coffreRepository;
    @Mock private AgioRepository agioRepository;
    @Mock private LigneEcritureComptableRepository ligneEcritureComptableRepository;

    @InjectMocks
    private ConformiteExtensionService conformiteExtensionService;

    @Test
    void creerAlerte_withValidData_shouldSucceed() {
        CreerAlerteServiceRequestDTO dto = new CreerAlerteServiceRequestDTO();
        dto.setTypeAlerte("TEST_ALERTE");
        dto.setNiveauRisque("MOYEN");
        dto.setResume("Test alerte");
        dto.setStatut("OUVERTE");

        when(alerteConformiteRepository.save(any(AlerteConformite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AlerteConformite resultat = conformiteExtensionService.creerAlerte(dto);

        assertNotNull(resultat);
        assertEquals("TEST_ALERTE", resultat.getTypeAlerte());
        assertEquals("MOYEN", resultat.getNiveauRisque());
        assertEquals("OUVERTE", resultat.getStatut());
    }

    @Test
    void creerAlerteInterne_withoutClientTransaction_shouldSucceed() {
        when(alerteConformiteRepository.save(any(AlerteConformite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AlerteConformite resultat = conformiteExtensionService.creerAlerteInterne(
                "INTERNAL_TEST", "Test interne", null, null, "FAIBLE"
        );

        assertNotNull(resultat);
        assertEquals("INTERNAL_TEST", resultat.getTypeAlerte());
        assertEquals("FAIBLE", resultat.getNiveauRisque());
        assertNull(resultat.getClient());
        assertNull(resultat.getTransaction());
    }

    @Test
    void analyserTransaction_withHighAmount_shouldCreateAlert() {
        Transaction transaction = new Transaction();
        transaction.setIdTransaction(1L);
        transaction.setMontantGlobal(new BigDecimal("1000000"));
        Client client = new Client();
        client.setIdClient(1L);
        client.setNiveauRisque(NiveauRisqueClient.FAIBLE);
        client.setNom("Dupont");
        client.setPrenom("Jean");

        assertDoesNotThrow(() -> conformiteExtensionService.analyserTransaction(transaction));
    }
}
