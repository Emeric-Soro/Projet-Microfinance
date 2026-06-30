package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.service.communication.event.VirementEffectueEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final CompteRepository compteRepository;

    public NotificationEventListener(
            NotificationService notificationService,
            EmailService emailService,
            CompteRepository compteRepository
    ) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.compteRepository = compteRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVirementEffectue(VirementEffectueEvent event) {
        // Envoi SMS via le service existant
        notificationService.envoyerAlerteVirement(event.numCompteDestination(), event.montant());
        
        // Envoi Email
        compteRepository.findByNumCompte(event.numCompteDestination()).ifPresent(compteDest -> {
            emailService.envoyerAlerteVirementRecu(compteDest, event.nomExpediteur(), event.montant(), compteDest.getSolde());
        });
    }
}
