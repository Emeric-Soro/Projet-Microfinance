package com.microfinance.core_banking.service.communication;

import com.microfinance.core_banking.entity.Client;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.Notification;
import com.microfinance.core_banking.entity.StatutEnvoi;
import com.microfinance.core_banking.entity.TypeCanal;
import com.microfinance.core_banking.repository.client.ClientRepository;
import com.microfinance.core_banking.repository.communication.NotificationRepository;
import com.microfinance.core_banking.repository.communication.StatutEnvoiRepository;
import com.microfinance.core_banking.repository.communication.TypeCanalRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TypeCanalRepository typeCanalRepository;

    @Mock
    private StatutEnvoiRepository statutEnvoiRepository;

    @Mock
    private NotificationDeliveryGateway notificationDeliveryGateway;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void shouldMoveNotificationFromPendingToSentOnSuccessfulDelivery() {
        Client client = buildClient("699001122");
        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-001");

        TypeCanal sms = buildCanal("SMS");
        StatutEnvoi enAttente = buildStatut("EN_ATTENTE");
        StatutEnvoi envoye = buildStatut("ENVOYE");

        List<NotificationSnapshot> snapshots = new ArrayList<>();
        when(compteRepository.findByNumCompte("CPT-001")).thenReturn(Optional.of(compte));
        when(typeCanalRepository.findByCodeCanal("SMS")).thenReturn(Optional.of(sms));
        when(statutEnvoiRepository.findByCodeStatutEnvoi("EN_ATTENTE")).thenReturn(Optional.of(enAttente));
        when(statutEnvoiRepository.findByCodeStatutEnvoi("ENVOYE")).thenReturn(Optional.of(envoye));
        when(notificationDeliveryGateway.envoyer(eq(client), eq(sms), any(String.class)))
                .thenReturn(NotificationDeliveryResult.succes());
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            snapshots.add(NotificationSnapshot.from(notification));
            return notification;
        });

        Notification notification = notificationService.envoyerAlerteVirement("CPT-001", new BigDecimal("15000"));

        assertThat(snapshots).hasSize(2);
        assertThat(snapshots.get(0).statutCode()).isEqualTo("EN_ATTENTE");
        assertThat(snapshots.get(0).dateEnvoi()).isNull();
        assertThat(snapshots.get(1).statutCode()).isEqualTo("ENVOYE");
        assertThat(snapshots.get(1).dateEnvoi()).isEqualTo(LocalDate.now());
        assertThat(snapshots.get(1).erreurEnvoi()).isNull();
        assertThat(notification.getStatutEnvoi().getCodeStatutEnvoi()).isEqualTo("ENVOYE");
    }

    @Test
    void shouldMoveNotificationToFailureWhenGatewayRejectsDelivery() {
        Client client = buildClient("");
        Compte compte = new Compte();
        compte.setClient(client);
        compte.setNumCompte("CPT-002");

        TypeCanal sms = buildCanal("SMS");
        StatutEnvoi enAttente = buildStatut("EN_ATTENTE");
        StatutEnvoi echec = buildStatut("ECHEC");

        List<NotificationSnapshot> snapshots = new ArrayList<>();
        when(compteRepository.findByNumCompte("CPT-002")).thenReturn(Optional.of(compte));
        when(typeCanalRepository.findByCodeCanal("SMS")).thenReturn(Optional.of(sms));
        when(statutEnvoiRepository.findByCodeStatutEnvoi("EN_ATTENTE")).thenReturn(Optional.of(enAttente));
        when(statutEnvoiRepository.findByCodeStatutEnvoi("ECHEC")).thenReturn(Optional.of(echec));
        when(notificationDeliveryGateway.envoyer(eq(client), eq(sms), any(String.class)))
                .thenReturn(NotificationDeliveryResult.echec("Aucun numero de telephone n'est renseigne pour ce client"));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            snapshots.add(NotificationSnapshot.from(notification));
            return notification;
        });

        Notification notification = notificationService.envoyerAlerteVirement("CPT-002", new BigDecimal("25000"));

        assertThat(snapshots).hasSize(2);
        assertThat(snapshots.get(0).statutCode()).isEqualTo("EN_ATTENTE");
        assertThat(snapshots.get(1).statutCode()).isEqualTo("ECHEC");
        assertThat(snapshots.get(1).erreurEnvoi()).isEqualTo("Aucun numero de telephone n'est renseigne pour ce client");
        assertThat(notification.getStatutEnvoi().getCodeStatutEnvoi()).isEqualTo("ECHEC");
        assertThat(notification.getErreurEnvoi()).isEqualTo("Aucun numero de telephone n'est renseigne pour ce client");
    }

    private Client buildClient(String telephone) {
        Client client = new Client();
        client.setCodeClient("CLI-20260427-9876");
        client.setTelephone(telephone);
        return client;
    }

    private TypeCanal buildCanal(String codeCanal) {
        TypeCanal canal = new TypeCanal();
        canal.setCodeCanal(codeCanal);
        canal.setLibelle(codeCanal);
        return canal;
    }

    private StatutEnvoi buildStatut(String codeStatut) {
        StatutEnvoi statut = new StatutEnvoi();
        statut.setCodeStatutEnvoi(codeStatut);
        statut.setLibelle(codeStatut);
        return statut;
    }

    private record NotificationSnapshot(
            String statutCode,
            LocalDate dateEnvoi,
            String erreurEnvoi
    ) {
        private static NotificationSnapshot from(Notification notification) {
            return new NotificationSnapshot(
                    notification.getStatutEnvoi().getCodeStatutEnvoi(),
                    notification.getDateEnvoi(),
                    notification.getErreurEnvoi()
            );
        }
    }
}
