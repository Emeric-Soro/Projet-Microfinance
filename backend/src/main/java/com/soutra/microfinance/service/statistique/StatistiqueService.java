package com.soutra.microfinance.service.statistique;

import com.soutra.microfinance.dto.response.statistique.StatKpiResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StatistiqueService {

    private final EntityManager entityManager;

    public StatistiqueService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public StatKpiResponseDTO getKpis() {
        long totalClientsActifs = getClientCountActif();

        BigDecimal totalDepots = getSumSoldes();

        BigDecimal totalCreditsEnCours = getSumCreditsActifs();

        BigDecimal tauxCreditsEnRetard = calculerPar(totalCreditsEnCours);

        return new StatKpiResponseDTO(totalClientsActifs, totalDepots, totalCreditsEnCours, tauxCreditsEnRetard);
    }

    private long getClientCountActif() {
        Query query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Client c WHERE UPPER(c.statutClient.libelleStatut) = 'ACTIF'"
        );
        return ((Number) query.getSingleResult()).longValue();
    }

    private BigDecimal getSumSoldes() {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(c.solde), 0) FROM Compte c"
        );
        return (BigDecimal) query.getSingleResult();
    }

    private BigDecimal getSumCreditsActifs() {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(cr.montantRestantDu), 0) FROM Credit cr " +
                        "WHERE cr.statutCredit.codeStatut NOT IN ('SOLDE', 'PASSE_EN_PERTE', 'CONTENTIEUX')"
        );
        return (BigDecimal) query.getSingleResult();
    }

    private BigDecimal calculerPar(BigDecimal totalCreditsEnCours) {
        if (totalCreditsEnCours.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(cr.montantRestantDu), 0) FROM Credit cr " +
                        "WHERE cr.statutCredit.codeStatut = 'EN_RETARD'"
        );
        BigDecimal creditsEnRetard = (BigDecimal) query.getSingleResult();

        return creditsEnRetard.multiply(new BigDecimal("100"))
                .divide(totalCreditsEnCours, 2, java.math.RoundingMode.HALF_UP);
    }
}
