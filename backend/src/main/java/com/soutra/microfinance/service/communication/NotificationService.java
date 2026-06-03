package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Notification;
import com.soutra.microfinance.entity.NotificationPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface NotificationService {

    Notification envoyerAlerteVirement(String numCompte, BigDecimal montant);

    Notification envoyerAlerteConnexionSuspecte(Long idClient);

    Notification envoyerCodeAuthentification(Long idClient, String codeOtp);

    Page<Notification> listerNotificationsClient(Long idClient, Pageable pageable);

    Notification consulterNotificationClient(Long idNotification, Long idClient);

    void marquerCommeLue(Long idNotification, Long idClient);

    NotificationPreference getPreferences(Long idClient);

    NotificationPreference updatePreferences(Long idClient, Boolean pushActif, Boolean smsActif, Boolean emailActif);
}
