package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Notification;

import java.math.BigDecimal;

public interface NotificationService {

    Notification envoyerAlerteVirement(String numCompte, BigDecimal montant);

    Notification envoyerAlerteConnexionSuspecte(Long idClient);

    Notification envoyerCodeAuthentification(Long idClient, String codeOtp);
}
