package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutCompte;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.compte.StatutCompteRepository;
import com.microfinance.core_banking.repository.compte.TypeCompteRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.EcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.JournalComptableRepository;
import com.microfinance.core_banking.repository.extension.CompteComptableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private LigneEcritureComptableRepository ligneEcritureComptableRepository;

    @Mock
    private EcritureComptableRepository ecritureComptableRepository;

    @Mock
    private JournalComptableRepository journalComptableRepository;

    @Mock
    private CompteComptableRepository compteComptableRepository;

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

    @Test
    void shouldBlockAndUnblockAccountByAddingStatusHistory() {
        Compte compte = new Compte();
        compte.setNumCompte("CPT-001");
        compte.getStatutsCompte().add(statut("ACTIF"));

        when(compteRepository.findByNumCompte("CPT-001")).thenReturn(Optional.of(compte));
        when(statutCompteRepository.save(any(StatutCompte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Compte bloque = compteService.bloquerCompte("CPT-001", "controle");
        Compte debloque = compteService.debloquerCompte("CPT-001", "ok");

        assertThat(bloque.getStatutsCompte()).extracting(StatutCompte::getLibelleStatut).contains("BLOQUE");
        assertThat(debloque.getStatutsCompte()).extracting(StatutCompte::getLibelleStatut).contains("ACTIF");
    }

    @Test
    void shouldRefuseClosingAccountWhenLedgerBalanceIsNotZero() {
        Compte compte = new Compte();
        compte.setNumCompte("CPT-002");
        compte.setDecouvertAutorise(new BigDecimal("1000"));
        compte.getStatutsCompte().add(statut("ACTIF"));

        LigneEcritureComptable ligne = new LigneEcritureComptable();
        ligne.setSens("CREDIT");
        ligne.setMontant(new BigDecimal("500"));

        when(compteRepository.findByNumCompte("CPT-002")).thenReturn(Optional.of(compte));
        when(ligneEcritureComptableRepository.findByReferenceAuxiliaire("CPT-002")).thenReturn(List.of(ligne));

        assertThatThrownBy(() -> compteService.cloturerCompte("CPT-002"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("solde non nul");
    }

    private StatutCompte statut(String libelle) {
        StatutCompte statut = new StatutCompte();
        statut.setLibelleStatut(libelle);
        statut.setDateStatut(LocalDateTime.now());
        return statut;
    }
}
