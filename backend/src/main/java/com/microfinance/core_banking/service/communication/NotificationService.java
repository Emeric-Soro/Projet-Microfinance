package com.microfinance.core_banking.service.communication;

import com.microfinance.core_banking.entity.Notification;

import java.math.BigDecimal;

public interface NotificationService {

    Notification envoyerAlerteVirement(String numCompte, BigDecimal montant);

    Notification envoyerAlerteConnexionSuspecte(Long idClient);
}
