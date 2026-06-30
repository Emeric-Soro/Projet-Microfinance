package com.soutra.microfinance.service.communication;

import com.soutra.microfinance.entity.CarteVisa;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.ReleveFormat;
import com.soutra.microfinance.repository.compte.CarteVisaRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.credit.EcheanceRepository;
import com.soutra.microfinance.service.compte.ReleveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Planificateur de tâches pour les envois d'emails périodiques ou par lots.
 */
@Component
public class EmailScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailScheduler.class);

    private final EcheanceRepository echeanceRepository;
    private final CompteRepository compteRepository;
    private final CarteVisaRepository carteVisaRepository;
    private final ReleveService releveService;
    private final EmailService emailService;

    public EmailScheduler(
            EcheanceRepository echeanceRepository,
            CompteRepository compteRepository,
            CarteVisaRepository carteVisaRepository,
            ReleveService releveService,
            EmailService emailService
    ) {
        this.echeanceRepository = echeanceRepository;
        this.compteRepository = compteRepository;
        this.carteVisaRepository = carteVisaRepository;
        this.releveService = releveService;
        this.emailService = emailService;
    }

    /**
     * Rappel d'échéances de crédit à venir dans 3 jours.
     * S'exécute tous les jours à 06:00.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void rappelerEcheancesCredit() {
        LOGGER.info("[CRON] Début du rappel des échéances de crédit (J+3)...");
        LocalDate cible = LocalDate.now().plusDays(3);
        List<Echeance> echeances = echeanceRepository.findByDateEcheanceAndEstPayeeFalse(cible);
        
        for (Echeance echeance : echeances) {
            try {
                BigDecimal restantDu = echeance.getMontantTotal().subtract(
                        echeance.getMontantPaye() != null ? echeance.getMontantPaye() : BigDecimal.ZERO
                );
                emailService.envoyerRappelEcheance(echeance.getCredit(), restantDu, echeance.getDateEcheance());
            } catch (Exception ex) {
                LOGGER.error("Erreur d'envoi du rappel échéance pour l'échéance ID: {}", echeance.getIdEcheance(), ex);
            }
        }
        LOGGER.info("[CRON] Fin du rappel des échéances de crédit ({} emails envoyés).", echeances.size());
    }

    /**
     * Alerte de retard de paiement (échéances manquées le jour d'avant).
     * S'exécute tous les jours à 07:00.
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void alerterRetardsCredit() {
        LOGGER.info("[CRON] Début de la détection des retards de paiement (J-1)...");
        LocalDate hier = LocalDate.now().minusDays(1);
        List<Echeance> echeances = echeanceRepository.findByDateEcheanceAndEstPayeeFalse(hier);
        
        for (Echeance echeance : echeances) {
            try {
                BigDecimal restantDu = echeance.getMontantTotal().subtract(
                        echeance.getMontantPaye() != null ? echeance.getMontantPaye() : BigDecimal.ZERO
                );
                BigDecimal penalite = echeance.getMontantPenalite() != null ? echeance.getMontantPenalite() : BigDecimal.ZERO;
                emailService.envoyerAlerteRetard(echeance.getCredit(), restantDu, echeance.getDateEcheance(), penalite);
            } catch (Exception ex) {
                LOGGER.error("Erreur d'envoi d'alerte retard pour l'échéance ID: {}", echeance.getIdEcheance(), ex);
            }
        }
        LOGGER.info("[CRON] Fin de la détection des retards de paiement ({} alertes envoyées).", echeances.size());
    }

    /**
     * Envoi automatique du relevé de compte périodique pour le mois précédent.
     * S'exécute le 1er de chaque mois à 01:00.
     */
    @Scheduled(cron = "0 0 1 1 * *")
    public void envoyerRelevesMensuels() {
        LOGGER.info("[CRON] Début de la génération et envoi des relevés mensuels...");
        LocalDate dateDebut = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate dateFin = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());
        
        List<Compte> comptes = compteRepository.findAll();
        int envoyes = 0;
        for (Compte compte : comptes) {
            if (compte.getClient() != null && compte.getClient().getEmail() != null && !compte.getClient().getEmail().isBlank()) {
                try {
                    byte[] pdf = releveService.genererReleve(compte.getNumCompte(), dateDebut, dateFin, ReleveFormat.PDF);
                    emailService.envoyerReleveCompte(compte, dateDebut, dateFin, pdf);
                    envoyes++;
                } catch (Exception ex) {
                    LOGGER.error("Erreur génération relevé mensuel pour le compte {}", compte.getNumCompte(), ex);
                }
            }
        }
        LOGGER.info("[CRON] Fin de l'envoi des relevés mensuels ({} relevés envoyés sur {} comptes).", envoyes, comptes.size());
    }

    /**
     * Alerte pour l'expiration imminente des cartes Visa (J+30).
     * S'exécute tous les jours à 08:00.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void notifierExpirationCartes() {
        LOGGER.info("[CRON] Début du contrôle des expirations de cartes VISA (J+30)...");
        LocalDate cible = LocalDate.now().plusDays(30);
        // On récupère les cartes expirant sur ce jour cible
        Page<CarteVisa> cartes = carteVisaRepository.findByDateExpirationBetween(cible, cible, PageRequest.of(0, 1000));
        
        for (CarteVisa carte : cartes) {
            try {
                String details = "Votre carte VISA numéro " + masquerNumeroCarte(carte.getNumeroCarte()) 
                        + " arrive à expiration le " + cible.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) 
                        + ". Une nouvelle carte sera bientôt mise à votre disposition en agence.";
                emailService.envoyerAlerteCarte(carte, "Expiration imminente", details);
            } catch (Exception ex) {
                LOGGER.error("Erreur d'alerte expiration pour la carte {}", carte.getNumeroCarte(), ex);
            }
        }
        LOGGER.info("[CRON] Fin du contrôle des expirations de cartes ({} alertes envoyées).", cartes.getNumberOfElements());
    }

    private String masquerNumeroCarte(String numeroCarte) {
        if (numeroCarte == null || numeroCarte.length() < 16) {
            return "4XXX XXXX XXXX XXXX";
        }
        return numeroCarte.substring(0, 4) + " XXXX XXXX " + numeroCarte.substring(12);
    }
}
