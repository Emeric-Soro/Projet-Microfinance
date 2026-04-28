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
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private static final String CODE_CANAL_SMS = "SMS";
    private static final String CODE_STATUT_EN_ATTENTE = "EN_ATTENTE";
    private static final String CODE_STATUT_ENVOYE = "ENVOYE";
    private static final String CODE_STATUT_ECHEC = "ECHEC";

    private final NotificationRepository notificationRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TypeCanalRepository typeCanalRepository;
    private final StatutEnvoiRepository statutEnvoiRepository;
    private final NotificationDeliveryGateway notificationDeliveryGateway;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            CompteRepository compteRepository,
            ClientRepository clientRepository,
            TypeCanalRepository typeCanalRepository,
            StatutEnvoiRepository statutEnvoiRepository,
            NotificationDeliveryGateway notificationDeliveryGateway
    ) {
        this.notificationRepository = notificationRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.typeCanalRepository = typeCanalRepository;
        this.statutEnvoiRepository = statutEnvoiRepository;
        this.notificationDeliveryGateway = notificationDeliveryGateway;
    }

    @Override
    @Transactional
    public Notification envoyerAlerteVirement(String numCompte, BigDecimal montant) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        String message = "Vous avez reçu un virement de " + montant + " FCFA sur le compte " + numCompte;
        return creerNotification(compte.getClient(), message);
    }

    @Override
    @Transactional
    public Notification envoyerAlerteConnexionSuspecte(Long idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        String message = "Alerte sécurité : une connexion suspecte a été détectée sur votre espace client.";
        return creerNotification(client, message);
    }

    @Override
    @Transactional
    public Notification envoyerCodeAuthentification(Long idClient, String codeOtp) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + idClient));

        String message = "Votre code d'authentification est " + codeOtp + ". Il expire dans 5 minutes.";
        return creerNotification(client, message);
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
