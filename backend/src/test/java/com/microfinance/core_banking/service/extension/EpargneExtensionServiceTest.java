package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.dto.request.extension.CloturerDatRequestDTO;
import com.microfinance.core_banking.dto.request.extension.CreerProduitEpargneServiceRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.DepotATerme;
import com.microfinance.core_banking.entity.ProduitEpargne;
import com.microfinance.core_banking.entity.Transaction;
import com.microfinance.core_banking.entity.TypeCompte;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.DepotATermeRepository;
import com.microfinance.core_banking.repository.extension.ProduitEpargneRepository;
import com.microfinance.core_banking.service.operation.TransactionService;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpargneExtensionServiceTest {

    @Mock private ProduitEpargneRepository produitEpargneRepository;
    @Mock private DepotATermeRepository depotATermeRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CompteRepository compteRepository;
    @Mock private TransactionService transactionService;
    @Mock private AuthenticatedUserService authenticatedUserService;
    @Captor private ArgumentCaptor<DepotATerme> depotCaptor;

    private EpargneExtensionService service;

    @BeforeEach
    void setUp() {
        service = new EpargneExtensionService(
                produitEpargneRepository, depotATermeRepository, clientRepository,
                compteRepository, transactionService, authenticatedUserService);
    }

    @Test
    void creerProduit_sauvegardeProduit() {
        CreerProduitEpargneServiceRequestDTO dto = new CreerProduitEpargneServiceRequestDTO();
        dto.setCodeProduit("EP-001");
        dto.setLibelle("Epargne Classique");
        dto.setCategorie("EPARGNE");
        dto.setTauxInteret(new BigDecimal("3.5"));
        dto.setDepotInitialMin(new BigDecimal("5000"));
        dto.setSoldeMinimum(new BigDecimal("1000"));
        dto.setFrequenceInteret("MENSUELLE");
        dto.setStatut("ACTIF");

        ProduitEpargne saved = new ProduitEpargne();
        saved.setIdProduitEpargne(1L);
        saved.setCodeProduit("EP-001");
        when(produitEpargneRepository.save(any(ProduitEpargne.class))).thenReturn(saved);

        ProduitEpargne resultat = service.creerProduit(dto);

        assertThat(resultat.getCodeProduit()).isEqualTo("EP-001");
        verify(produitEpargneRepository).save(any(ProduitEpargne.class));
    }

    @Test
    void calculerInteretsCourusMensuels_crediteComptesEpargne() {
        TypeCompte typeEpargne = new TypeCompte();
        typeEpargne.setLibelle("EPARGNE");

        Compte compte1 = new Compte();
        compte1.setNumCompte("SN-EP-001");
        compte1.setSolde(new BigDecimal("1000000"));
        compte1.setTauxInteret(new BigDecimal("5.0"));
        compte1.setTypeCompte(typeEpargne);

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("INTM-REF");

        when(compteRepository.findAll()).thenReturn(List.of(compte1));
        when(transactionService.posterDepotSysteme(anyString(), any(), any(), any(), anyString(), anyString()))
                .thenReturn(transaction);

        int resultat = service.calculerInteretsCourusMensuels(LocalDate.of(2026, 5, 1), 1L);

        assertThat(resultat).isEqualTo(1);
    }

    @Test
    void calculerInteretsCourusMensuels_ignoreComptesSansSolde() {
        TypeCompte typeEpargne = new TypeCompte();
        typeEpargne.setLibelle("EPARGNE");

        Compte compte = new Compte();
        compte.setNumCompte("SN-EP-002");
        compte.setSolde(BigDecimal.ZERO);
        compte.setTauxInteret(new BigDecimal("5.0"));
        compte.setTypeCompte(typeEpargne);

        when(compteRepository.findAll()).thenReturn(List.of(compte));

        int resultat = service.calculerInteretsCourusMensuels(LocalDate.of(2026, 5, 1), 1L);

        assertThat(resultat).isEqualTo(0);
    }

    @Test
    void calculerInteretsCourusMensuels_ignoreSansTaux() {
        TypeCompte typeEpargne = new TypeCompte();
        typeEpargne.setLibelle("EPARGNE");

        Compte compte = new Compte();
        compte.setNumCompte("SN-EP-003");
        compte.setSolde(new BigDecimal("500000"));
        compte.setTauxInteret(BigDecimal.ZERO);
        compte.setTypeCompte(typeEpargne);

        when(compteRepository.findAll()).thenReturn(List.of(compte));

        int resultat = service.calculerInteretsCourusMensuels(LocalDate.of(2026, 5, 1), 1L);

        assertThat(resultat).isEqualTo(0);
    }

    @Test
    void cloturerDepotATerme_clotureNormaleRembourseMontantPlusInterets() {
        Client client = new Client();
        client.setIdClient(10L);
        client.setAgence(new com.microfinance.core_banking.entity.Agence());
        client.getAgence().setIdAgence(1L);

        DepotATerme depot = new DepotATerme();
        depot.setIdDepotTerme(1L);
        depot.setReferenceDepot("DAT-001");
        depot.setClient(client);
        depot.setMontant(new BigDecimal("500000"));
        depot.setInteretsEstimes(new BigDecimal("25000"));
        depot.setStatut("ACTIF");
        depot.setDateSouscription(LocalDate.of(2025, 10, 1));
        depot.setDateEcheance(LocalDate.of(2026, 10, 1));

        Transaction transaction = new Transaction();
        transaction.setReferenceUnique("DATCLO-REF");

        when(depotATermeRepository.findById(1L)).thenReturn(Optional.of(depot));
        when(depotATermeRepository.save(any(DepotATerme.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionService.posterDepotSysteme(anyString(), any(), any(), any(), anyString(), anyString()))
                .thenReturn(transaction);

        CloturerDatRequestDTO dto = new CloturerDatRequestDTO();
        dto.setIdUtilisateurOperateur(5L);

        DepotATerme resultat = service.cloturerDepotATerme(1L, dto);

        assertThat(resultat.getStatut()).isEqualTo("CLOTURE");
    }

    @Test
    void cloturerDepotATerme_refuseDejaCloture() {
        DepotATerme depot = new DepotATerme();
        depot.setIdDepotTerme(2L);
        depot.setStatut("CLOTURE");

        when(depotATermeRepository.findById(2L)).thenReturn(Optional.of(depot));

        CloturerDatRequestDTO dto = new CloturerDatRequestDTO();

        assertThatThrownBy(() -> service.cloturerDepotATerme(2L, dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("deja cloture");
    }
}
