package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.StatutCompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompteServiceImplTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TypeCompteRepository typeCompteRepository;

    @Mock
    private StatutCompteRepository statutCompteRepository;

    @InjectMocks
    private CompteServiceImpl compteService;

    @Test
    void shouldReturnOpenedAccountWithCurrentStatusAttached() {
        Client client = new Client();
        client.setIdClient(7L);
        client.setStatutKyc(StatutKycClient.VALIDE);
        StatutClient statutClient = new StatutClient();
        statutClient.setLibelleStatut("ACTIF");
        client.setStatutClient(statutClient);

        TypeCompte typeCompte = new TypeCompte();
        typeCompte.setLibelle("COURANT");

        when(clientRepository.findById(7L)).thenReturn(Optional.of(client));
        when(typeCompteRepository.findByLibelleIgnoreCase("COURANT")).thenReturn(Optional.of(typeCompte));
        when(compteRepository.countByClient_IdClient(7L)).thenReturn(0L);
        when(compteRepository.existsByNumCompte("CPT-" + java.time.LocalDate.now().toString().replace("-", "") + "-000007-01")).thenReturn(false);
        when(compteRepository.save(any(Compte.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(statutCompteRepository.save(any(StatutCompte.class))).thenAnswer(invocation -> {
            StatutCompte statut = invocation.getArgument(0);
            statut.setIdStatutCompte(1L);
            if (statut.getDateStatut() == null) {
                statut.setDateStatut(LocalDateTime.now());
            }
            return statut;
        });

        Compte compte = compteService.ouvrirCompte(7L, "COURANT", new BigDecimal("10000.00"));

        assertThat(compte.getStatutsCompte()).hasSize(1);
        assertThat(compte.getStatutsCompte().get(0).getLibelleStatut()).isEqualTo("ACTIF");
    }
}
