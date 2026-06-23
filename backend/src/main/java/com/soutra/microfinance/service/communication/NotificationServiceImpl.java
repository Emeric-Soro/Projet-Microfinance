package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Client;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.NotificationPreference;
import com.soutra.microfinance.entity.StatutEnvoi;
import com.soutra.microfinance.entity.TypeCanal;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.communication.NotificationPreferenceRepository;
import com.soutra.microfinance.repository.communication.NotificationRepository;
import com.soutra.microfinance.repository.communication.StatutEnvoiRepository;
import com.soutra.microfinance.repository.communication.TypeCanalRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String CODE_CANAL_SMS = "SMS";
    private static final String CODE_STATUT_EN_ATTENTE = "EN_ATTENTE";
    private static final String CODE_STATUT_ENVOYE = "ENVOYE";
    private static final String CODE_STATUT_ECHEC = "ECHEC";

    @Value("${app.sms.template.virement}")
    private String templateVirement;

    @Value("${app.sms.template.alerte-securite}")
    private String templateAlerteSecurite;

    @Value("${app.sms.template.otp}")
    private String templateOtp;

    private final NotificationRepository notificationRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TypeCanalRepository typeCanalRepository;
    private final StatutEnvoiRepository statutEnvoiRepository;
    private final NotificationDeliveryGateway notificationDeliveryGateway;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            CompteRepository compteRepository,
            ClientRepository clientRepository,
            TypeCanalRepository typeCanalRepository,
            StatutEnvoiRepository statutEnvoiRepository,
            NotificationDeliveryGateway notificationDeliveryGateway,
            NotificationPreferenceRepository notificationPreferenceRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.typeCanalRepository = typeCanalRepository;
        this.statutEnvoiRepository = statutEnvoiRepository;
        this.notificationDeliveryGateway = notificationDeliveryGateway;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    @Transactional
    public Notification envoyerAlerteVirement(String numCompte, BigDecimal montant) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        String message = String.format(templateVirement, montant, numCompte);
        return creerNotification(compte.getClient(), message);
    }

    @Override
    @Transactional
    public Notification envoyerAlerteConnexionSuspecte(Long idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        String message = templateAlerteSecurite;
        return creerNotification(client, message);
    }

    @Override
    @Transactional
    public Notification envoyerCodeAuthentification(Long idClient, String codeOtp) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        String message = String.format(templateOtp, codeOtp);
        return creerNotification(client, message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> listerNotificationsClient(Long idClient, Pageable pageable) {
        if (!clientRepository.existsById(idClient)) {
            throw new EntityNotFoundException("Client introuvable: " + idClient);
        }
        return notificationRepository.findByClient_IdClient(idClient, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long compterNotificationsNonLues(Long idClient) {
        if (!clientRepository.existsById(idClient)) {
            throw new EntityNotFoundException("Client introuvable: " + idClient);
        }
        return notificationRepository.countByClient_IdClientAndLuFalse(idClient);
    }

    @Override
    @Transactional(readOnly = true)
    public Notification consulterNotificationClient(Long idNotification, Long idClient) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new EntityNotFoundException("Notification introuvable: " + idNotification));
        if (!notification.getClient().getIdClient().equals(idClient)) {
            throw new EntityNotFoundException("Notification introuvable: " + idNotification);
        }
        return notification;
    }

    @Override
    @Transactional
    public void marquerCommeLue(Long idNotification, Long idClient) {
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new EntityNotFoundException("Notification introuvable: " + idNotification));
        if (!notification.getClient().getIdClient().equals(idClient)) {
            throw new EntityNotFoundException("Notification introuvable: " + idNotification);
        }
        if (Boolean.TRUE.equals(notification.getLu())) {
            return;
        }
        notification.setLu(Boolean.TRUE);
        notification.setLueLe(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPreference getPreferences(Long idClient) {
        return notificationPreferenceRepository.findByIdClient(idClient)
                .orElseGet(() -> preferencesParDefaut(idClient));
    }

    @Override
    @Transactional
    public NotificationPreference updatePreferences(Long idClient, Boolean pushActif, Boolean smsActif, Boolean emailActif) {
        NotificationPreference preferences = notificationPreferenceRepository.findByIdClient(idClient)
                .orElseGet(() -> preferencesParDefaut(idClient));
        preferences.setPushActif(pushActif);
        preferences.setSmsActif(smsActif);
        preferences.setEmailActif(emailActif);
        return notificationPreferenceRepository.save(preferences);
    }

    private NotificationPreference preferencesParDefaut(Long idClient) {
        NotificationPreference defaut = new NotificationPreference();
        defaut.setIdClient(idClient);
        defaut.setPushActif(Boolean.TRUE);
        defaut.setSmsActif(Boolean.TRUE);
        defaut.setEmailActif(Boolean.TRUE);
        return defaut;
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private Notification creerNotification(Client client, String message) {
        TypeCanal canal = chargerCanalStrict(CODE_CANAL_SMS);
        StatutEnvoi statutEnAttente = chargerStatutStrict(CODE_STATUT_EN_ATTENTE);

        Notification notification = new Notification();
        notification.setClient(client);
        notification.setTypeCanal(canal);
        notification.setStatutEnvoi(statutEnAttente);
        notification.setMessage(message);
        notification.setDateEnvoi(null);
        notification.setErreurEnvoi(null);

        Notification notificationPersisted = notificationRepository.save(notification);
        NotificationDeliveryResult resultat = notificationDeliveryGateway.envoyer(client, canal, message);

        if (resultat.reussi()) {
            notificationPersisted.setStatutEnvoi(chargerStatutStrict(CODE_STATUT_ENVOYE));
            notificationPersisted.setDateEnvoi(LocalDate.now());
            notificationPersisted.setErreurEnvoi(null);
            return notificationRepository.save(notificationPersisted);
        }

        LOGGER.warn(
                "Echec d'envoi de la notification pour le client {} via {}: {}",
                client.getCodeClient(),
                canal.getCodeCanal(),
                resultat.erreur()
        );
        notificationPersisted.setStatutEnvoi(chargerStatutStrict(CODE_STATUT_ECHEC));
        notificationPersisted.setDateEnvoi(null);
        notificationPersisted.setErreurEnvoi(resultat.erreur());
        return notificationRepository.save(notificationPersisted);
    }

    private TypeCanal chargerCanalStrict(String codeCanal) {
        return typeCanalRepository.findByCodeCanal(codeCanal)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le canal de communication '" + codeCanal + "' n'est pas configuré."));
    }

    private StatutEnvoi chargerStatutStrict(String codeStatut) {
        return statutEnvoiRepository.findByCodeStatutEnvoi(codeStatut)
                .orElseThrow(() -> new IllegalStateException("Alerte Système : Le statut d'envoi '" + codeStatut + "' n'est pas configuré."));
    }
}
