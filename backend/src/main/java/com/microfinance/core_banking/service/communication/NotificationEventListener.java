package com.microfinance.core_banking.service.communication;

import com.microfinance.core_banking.service.communication.event.VirementEffectueEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVirementEffectue(VirementEffectueEvent event) {
        notificationService.envoyerAlerteVirement(event.numCompteDestination(), event.montant());
    }
}
