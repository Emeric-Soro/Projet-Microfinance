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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final TypeCanalRepository typeCanalRepository;
    private final StatutEnvoiRepository statutEnvoiRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            CompteRepository compteRepository,
            ClientRepository clientRepository,
            TypeCanalRepository typeCanalRepository,
            StatutEnvoiRepository statutEnvoiRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.typeCanalRepository = typeCanalRepository;
        this.statutEnvoiRepository = statutEnvoiRepository;
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

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private Notification creerNotification(Client client, String message) {
        TypeCanal canal = chargerCanalStrict("SMS");
        StatutEnvoi statut = chargerStatutStrict("ENVOYE");

        Notification notification = new Notification();
        notification.setClient(client);
        notification.setTypeCanal(canal);
        notification.setStatutEnvoi(statut);
        notification.setMessage(message);
        notification.setDateEnvoi(LocalDate.now()); // Note : LocalDateTime.now() serait encore plus précis pour une alerte !

        return notificationRepository.save(notification);
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