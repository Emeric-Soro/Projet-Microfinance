package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.entity.EcritureComptable;
import com.microfinance.core_banking.entity.LigneEcritureComptable;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.entity.SensEcriture;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import com.microfinance.core_banking.repository.extension.EcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.LigneEcritureComptableRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DataIntegrityAuditor {

    private final EcritureComptableRepository ecritureComptableRepository;
    private final LigneEcritureComptableRepository ligneEcritureComptableRepository;
    private final CompteRepository compteRepository;
    private final SessionCaisseRepository sessionCaisseRepository;

    public DataIntegrityAuditor(EcritureComptableRepository ecritureComptableRepository,
                                LigneEcritureComptableRepository ligneEcritureComptableRepository,
                                CompteRepository compteRepository,
                                SessionCaisseRepository sessionCaisseRepository) {
        this.ecritureComptableRepository = ecritureComptableRepository;
        this.ligneEcritureComptableRepository = ligneEcritureComptableRepository;
        this.compteRepository = compteRepository;
        this.sessionCaisseRepository = sessionCaisseRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> verifierIntegrite() {
        Map<String, Object> rapport = new LinkedHashMap<>();
        rapport.put("horodatage", LocalDateTime.now().toString());

        verifierEquilibreGeneral(rapport);
        verifierEcrituresDesequilibrees(rapport);
        verifierEcrituresSansLignes(rapport);
        verifierSessionsBloquees(rapport);
        verifierComptesSansEcritures(rapport);

        long totalAnomalies = ((List<?>) rapport.getOrDefault("ecrituresDesequilibrees", List.of())).size()
                + (Integer) rapport.getOrDefault("ecrituresSansLignes", 0)
                + (Integer) rapport.getOrDefault("sessionsBloquees", 0)
                + (Integer) rapport.getOrDefault("comptesSansEcritures", 0);

        rapport.put("totalAnomalies", totalAnomalies);
        rapport.put("statut", totalAnomalies == 0 ? "OK" : "ANOMALIES_DETECTEES");
        return rapport;
    }

    private void verifierEquilibreGeneral(Map<String, Object> rapport) {
        List<EcritureComptable> toutes = ecritureComptableRepository.findAll();
        List<LigneEcritureComptable> toutesLignes = ligneEcritureComptableRepository.findAll();

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        for (LigneEcritureComptable l : toutesLignes) {
            if ("DEBIT".equalsIgnoreCase(l.getSens())) {
                totalDebit = totalDebit.add(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
            } else {
                totalCredit = totalCredit.add(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
            }
        }

        rapport.put("totalEcritures", toutes.size());
        rapport.put("totalLignes", toutesLignes.size());
        rapport.put("totalDebit", totalDebit);
        rapport.put("totalCredit", totalCredit);
        rapport.put("equilibreGlobal", totalDebit.compareTo(totalCredit) == 0);
    }

    private void verifierEcrituresDesequilibrees(Map<String, Object> rapport) {
        List<EcritureComptable> toutes = ecritureComptableRepository.findAll();
        List<Map<String, Object>> desequilibres = new ArrayList<>();

        for (EcritureComptable ecriture : toutes) {
            List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository
                    .findByEcritureComptable_IdEcritureComptable(ecriture.getIdEcritureComptable());

            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;
            for (LigneEcritureComptable l : lignes) {
                if ("DEBIT".equalsIgnoreCase(l.getSens())) {
                    debit = debit.add(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
                } else {
                    credit = credit.add(l.getMontant() != null ? l.getMontant() : BigDecimal.ZERO);
                }
            }

            if (debit.compareTo(credit) != 0) {
                Map<String, Object> anomaly = new LinkedHashMap<>();
                anomaly.put("referencePiece", ecriture.getReferencePiece());
                anomaly.put("dateComptable", ecriture.getDateComptable().toString());
                anomaly.put("totalDebit", debit);
                anomaly.put("totalCredit", credit);
                anomaly.put("ecart", debit.subtract(credit));
                desequilibres.add(anomaly);
            }
        }

        rapport.put("ecrituresDesequilibrees", desequilibres);
    }

    private void verifierEcrituresSansLignes(Map<String, Object> rapport) {
        List<EcritureComptable> toutes = ecritureComptableRepository.findAll();
        int sansLignes = 0;
        for (EcritureComptable e : toutes) {
            List<LigneEcritureComptable> lignes = ligneEcritureComptableRepository
                    .findByEcritureComptable_IdEcritureComptable(e.getIdEcritureComptable());
            if (lignes.isEmpty()) sansLignes++;
        }
        rapport.put("ecrituresSansLignes", sansLignes);
    }

    private void verifierSessionsBloquees(Map<String, Object> rapport) {
        List<SessionCaisse> sessions = sessionCaisseRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        long sessionsBloquees = sessions.stream()
                .filter(s -> "OUVERTE".equalsIgnoreCase(s.getStatut()))
                .filter(s -> s.getDateOuverture() != null
                        && s.getDateOuverture().plusHours(24).isBefore(now))
                .count();
        rapport.put("sessionsBloquees", (int) sessionsBloquees);
    }

    private void verifierComptesSansEcritures(Map<String, Object> rapport) {
        List<Compte> comptes = compteRepository.findAll();
        Set<String> comptesAvecEcritures = ligneEcritureComptableRepository.findAll()
                .stream()
                .map(LigneEcritureComptable::getReferenceAuxiliaire)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        long sansEcritures = comptes.stream()
                .filter(c -> !comptesAvecEcritures.contains(c.getNumCompte()))
                .count();
        rapport.put("comptesSansEcritures", (int) sansEcritures);
    }
}
