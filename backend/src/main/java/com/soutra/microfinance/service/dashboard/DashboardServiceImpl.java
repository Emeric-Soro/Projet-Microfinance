package com.soutra.microfinance.service.dashboard;

import com.soutra.microfinance.dto.response.statistique.DashboardAgenceResponseDTO;
import com.soutra.microfinance.dto.response.statistique.DashboardDirectionResponseDTO;
import com.soutra.microfinance.dto.response.statistique.IndicateurTempsReelResponseDTO;
import com.soutra.microfinance.entity.Caisse;
import com.soutra.microfinance.entity.StatutOperation;
import com.soutra.microfinance.repository.client.ClientRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import com.soutra.microfinance.repository.credit.CreditRepository;
import com.soutra.microfinance.repository.credit.EcheanceRepository;
import com.soutra.microfinance.repository.operation.CaisseRepository;
import com.soutra.microfinance.repository.operation.TransactionRepository;
import com.soutra.microfinance.repository.parametrage.AgenceRepository;
import com.soutra.microfinance.repository.client.UtilisateurRepository;
import com.soutra.microfinance.service.client.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final EcheanceRepository echeanceRepository;
    private final CaisseRepository caisseRepository;
    private final AgenceRepository agenceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SessionService sessionService;

    public DashboardServiceImpl(TransactionRepository transactionRepository,
                                CompteRepository compteRepository,
                                ClientRepository clientRepository,
                                CreditRepository creditRepository,
                                EcheanceRepository echeanceRepository,
                                CaisseRepository caisseRepository,
                                AgenceRepository agenceRepository,
                                UtilisateurRepository utilisateurRepository,
                                SessionService sessionService) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.echeanceRepository = echeanceRepository;
        this.caisseRepository = caisseRepository;
        this.agenceRepository = agenceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.sessionService = sessionService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardAgenceResponseDTO getKpisAgence(Long agenceId, String periode) {
        // Resolve agenceId from connected user if not provided
        if (agenceId == null) {
            org.springframework.security.core.Authentication auth =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                var utilisateurOpt = utilisateurRepository.findByLogin(auth.getName());
                if (utilisateurOpt.isPresent() && utilisateurOpt.get().getAgence() != null) {
                    agenceId = utilisateurOpt.get().getAgence().getIdAgence();
                }
            }
            if (agenceId == null) {
                // Fallback: use the first available agence
                var allAgences = agenceRepository.findAll();
                if (!allAgences.isEmpty()) {
                    agenceId = allAgences.get(0).getIdAgence();
                } else {
                    throw new IllegalArgumentException("Aucune agence disponible pour le dashboard");
                }
            }
        }
        LocalDate today = LocalDate.now();
        LocalDateTime debutJour = today.atStartOfDay();
        LocalDateTime finJour = today.atTime(LocalTime.MAX);

        LocalDate debutMois = today.withDayOfMonth(1);
        LocalDateTime debutMoisDateTime = debutMois.atStartOfDay();
        LocalDateTime finMoisDateTime = today.atTime(LocalTime.MAX);

        // Clients du jour pour cette agence
        long totalClientsJour = clientRepository.countByAgenceAndDateInscriptionAfter(agenceId, debutMois);

        // Depots du jour pour l'agence
        BigDecimal totalDepotsJour = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatutAndAgence(
                "DEPOT", debutJour, finJour, StatutOperation.EXECUTEE, agenceId);

        // Retraits du jour
        BigDecimal totalRetraitsJour = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatutAndAgence(
                "RETRAIT", debutJour, finJour, StatutOperation.EXECUTEE, agenceId);

        // Credits accordes du mois pour l'agence
        BigDecimal totalCreditsAccordesMois = creditRepository.sumMontantAccordeByDateDecaissementBetween(
                debutMois, today);

        // Encours credit pour cette agence
        BigDecimal encoursCredit = creditRepository.sumMontantRestantDu();

        // Solde caisse pour cette agence
        BigDecimal soldeCaisse = caisseRepository.sumSoldeCourantByStatut(Caisse.StatutCaisse.OUVERTE);

        // Operations en attente
        long nbOperationsEnAttente = transactionRepository.countByStatutOperation(StatutOperation.EN_ATTENTE);

        // Nom de l'agence
        String agenceNom = agenceRepository.findById(agenceId)
                .map(agence -> agence.getNom())
                .orElse("Agence #" + agenceId);

        return new DashboardAgenceResponseDTO(
                agenceId,
                agenceNom,
                today,
                totalClientsJour,
                totalDepotsJour,
                totalRetraitsJour,
                totalCreditsAccordesMois,
                encoursCredit,
                soldeCaisse,
                nbOperationsEnAttente
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDirectionResponseDTO getKpisDirection() {
        LocalDate today = LocalDate.now();

        // Agences actives
        long nbAgencesActives = agenceRepository.findByEstActiveTrue().size();

        // Total clients reseau
        long totalClientsReseau = clientRepository.count();

        // Depots/retraits totaux (toutes transactions executees cumulees)
        LocalDateTime debutJour = today.atStartOfDay();
        LocalDateTime finJour = today.atTime(LocalTime.MAX);

        BigDecimal totalDepotsReseau = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "DEPOT", debutJour, finJour, StatutOperation.EXECUTEE);
        BigDecimal totalRetraitsReseau = transactionRepository.sumMontantByTypeCodeDateBetweenAndStatut(
                "RETRAIT", debutJour, finJour, StatutOperation.EXECUTEE);

        // Credits totaux reseau
        BigDecimal totalCreditsReseau = creditRepository.sumMontantAccorde();
        BigDecimal encoursTotalReseau = creditRepository.sumMontantRestantDu();

        // PAR Global
        LocalDate dateSeuil30 = today.minusDays(30);
        BigDecimal par30 = echeanceRepository.sumCapitalImpayesBeforeDate(dateSeuil30);
        BigDecimal parGlobal = BigDecimal.ZERO;
        if (encoursTotalReseau.compareTo(BigDecimal.ZERO) > 0) {
            parGlobal = par30.multiply(new BigDecimal("100"))
                    .divide(encoursTotalReseau, 2, RoundingMode.HALF_UP);
        }

        // Ratio efficacite global (frais du jour / volume transactions du jour)
        BigDecimal fraisJour = transactionRepository.sumFraisByDateBetweenAndStatut(
                debutJour, finJour, StatutOperation.EXECUTEE);
        BigDecimal volumeJour = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debutJour, finJour, StatutOperation.EXECUTEE);
        BigDecimal ratioEfficaciteGlobal = BigDecimal.ZERO;
        if (volumeJour.compareTo(BigDecimal.ZERO) > 0) {
            ratioEfficaciteGlobal = fraisJour.multiply(new BigDecimal("100"))
                    .divide(volumeJour, 2, RoundingMode.HALF_UP);
        }

        // Utilisateurs actifs (via sessions)
        long nbUtilisateursActifs = sessionService.countSessionsActives();

        return new DashboardDirectionResponseDTO(
                today,
                nbAgencesActives,
                totalClientsReseau,
                totalDepotsReseau,
                totalRetraitsReseau,
                totalCreditsReseau,
                encoursTotalReseau,
                parGlobal,
                ratioEfficaciteGlobal,
                nbUtilisateursActifs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public IndicateurTempsReelResponseDTO getIndicateursTempsReel() {
        LocalDate today = LocalDate.now();
        LocalDateTime debutJour = today.atStartOfDay();
        LocalDateTime finJour = today.atTime(LocalTime.MAX);

        long totalClients = clientRepository.count();
        long totalComptes = compteRepository.count();
        long totalTransactionsJour = transactionRepository.countByDateHeureTransactionBetween(debutJour, finJour);
        BigDecimal montantTotalTransactionsJour = transactionRepository.sumMontantGlobalByDateBetweenAndStatut(
                debutJour, finJour, StatutOperation.EXECUTEE);
        long nbSessionsActives = sessionService.countSessionsActives();

        return new IndicateurTempsReelResponseDTO(
                totalClients,
                totalComptes,
                totalTransactionsJour,
                montantTotalTransactionsJour,
                nbSessionsActives,
                LocalDateTime.now()
        );
    }
}
