package com.microfinance.core_banking.service.client;

import com.microfinance.core_banking.dto.request.client.DecisionKycClientRequestDTO;
import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import com.microfinance.core_banking.entity.TypePieceIdentite;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.client.StatutClientRepository;
import com.microfinance.core_banking.service.security.AuthenticatedUserService;
import com.microfinance.core_banking.service.extension.ConformiteExtensionService;
import com.microfinance.core_banking.service.extension.AmlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatutClientRepository statutClientRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @Mock
    private ConformiteExtensionService conformiteExtensionService;

    @Mock
    private AmlService amlService;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void shouldCreateClientWithKycPendingWhenDossierIsComplete() {
        Client client = buildClientComplet();
        StatutClient statutNouveau = buildStatut("NOUVEAU");

        when(clientRepository.existsByEmail("client@example.com")).thenReturn(false);
        when(clientRepository.existsByTelephone("+221770000000")).thenReturn(false);
        when(clientRepository.existsByNumeroPieceIdentite("ID778899")).thenReturn(false);
        when(clientRepository.existsByCodeClient(any(String.class))).thenReturn(false);
        when(statutClientRepository.findByLibelleStatutIgnoreCase("NOUVEAU")).thenReturn(Optional.of(statutNouveau));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client resultat = clientService.creerClient(client);

        assertThat(resultat.getStatutKyc()).isEqualTo(StatutKycClient.EN_ATTENTE);
        assertThat(resultat.getDateSoumissionKyc()).isEqualTo(LocalDate.now());
        assertThat(resultat.getNiveauRisque()).isEqualTo(NiveauRisqueClient.FAIBLE);
        assertThat(resultat.getStatutClient()).isEqualTo(statutNouveau);
    }

    @Test
    void shouldApproveKycAndActivateClient() {
        Client client = buildClientComplet();
        client.setIdClient(5L);
        client.setStatutKyc(StatutKycClient.EN_ATTENTE);
        client.setStatutClient(buildStatut("NOUVEAU"));

        StatutClient statutActif = buildStatut("ACTIF");
        DecisionKycClientRequestDTO requestDTO = new DecisionKycClientRequestDTO(
                StatutKycClient.VALIDE,
                NiveauRisqueClient.MODERE,
                "Dossier conforme",
                "SUP-001"
        );

        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));
        when(statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")).thenReturn(Optional.of(statutActif));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Client resultat = clientService.traiterDossierKyc(5L, requestDTO);

        assertThat(resultat.getStatutKyc()).isEqualTo(StatutKycClient.VALIDE);
        assertThat(resultat.getNiveauRisque()).isEqualTo(NiveauRisqueClient.MODERE);
        assertThat(resultat.getStatutClient()).isEqualTo(statutActif);
        assertThat(resultat.getValidateurKyc()).isEqualTo("SUP-001");
        assertThat(resultat.getDateValidationKyc()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldFailKycValidationWhenTargetStatusIsNotConfigured() {
        Client client = buildClientComplet();
        client.setIdClient(5L);
        client.setStatutKyc(StatutKycClient.EN_ATTENTE);
        client.setStatutClient(buildStatut("NOUVEAU"));

        DecisionKycClientRequestDTO requestDTO = new DecisionKycClientRequestDTO(
                StatutKycClient.VALIDE,
                NiveauRisqueClient.MODERE,
                "Dossier conforme",
                "SUP-001"
        );

        when(clientRepository.findById(5L)).thenReturn(Optional.of(client));
        when(statutClientRepository.findByLibelleStatutIgnoreCase("ACTIF")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.traiterDossierKyc(5L, requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Erreur critique : Le statut 'ACTIF' n'est pas paramétré en base.");
    }

    private Client buildClientComplet() {
        Client client = new Client();
        client.setNom("Doe");
        client.setPrenom("Jane");
        client.setAdresse("Dakar");
        client.setEmail("client@example.com");
        client.setTelephone("+221770000000");
        client.setDateNaissance(LocalDate.of(1990, 5, 12));
        client.setProfession("Commercante");
        client.setEmployeur("Marche Central");
        client.setTypePieceIdentite(TypePieceIdentite.CNI);
        client.setNumeroPieceIdentite("ID778899");
        client.setDateExpirationPieceIdentite(LocalDate.now().plusYears(3));
        client.setPhotoIdentiteUrl("https://cdn.example.com/photo.jpg");
        client.setJustificatifDomicileUrl("https://cdn.example.com/dom.pdf");
        client.setJustificatifRevenusUrl("https://cdn.example.com/revenus.pdf");
        client.setPaysNationalite("Senegal");
        client.setPaysResidence("Senegal");
        client.setPep(false);
        return client;
    }

    private StatutClient buildStatut(String libelle) {
        StatutClient statutClient = new StatutClient();
        statutClient.setLibelleStatut(libelle);
        statutClient.setDateStatut(LocalDateTime.now());
        return statutClient;
    }
}
