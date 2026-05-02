package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.BilletageCaisse;
import com.microfinance.core_banking.entity.Caisse;
import com.microfinance.core_banking.entity.SessionCaisse;
import com.microfinance.core_banking.repository.extension.BilletageCaisseRepository;
import com.microfinance.core_banking.repository.extension.CaisseRepository;
import com.microfinance.core_banking.repository.extension.SessionCaisseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CaisseExtensionService {

    private final CaisseRepository caisseRepository;
    private final SessionCaisseRepository sessionCaisseRepository;
    private final BilletageCaisseRepository billetageCaisseRepository;

    public CaisseExtensionService(
            CaisseRepository caisseRepository,
            SessionCaisseRepository sessionCaisseRepository,
            BilletageCaisseRepository billetageCaisseRepository
    ) {
        this.caisseRepository = caisseRepository;
        this.sessionCaisseRepository = sessionCaisseRepository;
        this.billetageCaisseRepository = billetageCaisseRepository;
    }

    @Transactional(readOnly = true)
    public List<Caisse> listerCaisses() {
        return caisseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Caisse> consulterCaisse(Long id) {
        return caisseRepository.findById(id);
    }

    @Transactional
    public BilletageCaisse enregistrerBilletage(
            Long idSessionCaisse, BigDecimal coupure, Integer quantite, String typeBilletage
    ) {
        SessionCaisse session = sessionCaisseRepository.findById(idSessionCaisse)
                .orElseThrow(() -> new EntityNotFoundException("Session caisse introuvable"));
        if (!"OUVERTE".equalsIgnoreCase(session.getStatut())) {
            throw new IllegalStateException("La session caisse doit etre ouverte pour enregistrer un billetage");
        }
        BilletageCaisse billetage = new BilletageCaisse();
        billetage.setSessionCaisse(session);
        billetage.setCoupure(coupure);
        billetage.setQuantite(quantite);
        billetage.setTotal(coupure.multiply(BigDecimal.valueOf(quantite)));
        billetage.setTypeBilletage(typeBilletage != null ? typeBilletage : "BILLET");
        billetage.setDateBilletage(LocalDateTime.now());
        return billetageCaisseRepository.save(billetage);
    }

    @Transactional(readOnly = true)
    public List<BilletageCaisse> listerBilletageParSession(Long idSessionCaisse) {
        return billetageCaisseRepository.findBySessionCaisse_IdSessionCaisse(idSessionCaisse);
    }

    @Transactional(readOnly = true)
    public List<BilletageCaisse> listerBilletageParCaisse(Long idCaisse) {
        return billetageCaisseRepository.findBySessionCaisse_Caisse_IdCaisseOrderByDateBilletageDesc(idCaisse);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> genererArreteCaisse(Long idCaisse, LocalDate dateArrete) {
        Caisse caisse = caisseRepository.findById(idCaisse)
                .orElseThrow(() -> new EntityNotFoundException("Caisse introuvable"));
        LocalDate date = dateArrete != null ? dateArrete : LocalDate.now();
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = date.atTime(LocalTime.MAX);

        List<SessionCaisse> sessions = sessionCaisseRepository.findByCaisse_IdCaisseOrderByDateOuvertureDesc(idCaisse);
        List<SessionCaisse> sessionsDuJour = sessions.stream()
                .filter(s -> s.getDateOuverture().toLocalDate().equals(date))
                .toList();

        BigDecimal totalSoldeTheorique = sessionsDuJour.stream()
                .map(s -> s.getSoldeTheoriqueFermeture() != null ? s.getSoldeTheoriqueFermeture() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSoldePhysique = sessionsDuJour.stream()
                .map(s -> s.getSoldePhysiqueFermeture() != null ? s.getSoldePhysiqueFermeture() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEcart = sessionsDuJour.stream()
                .map(s -> s.getEcart() != null ? s.getEcart() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long nbSessionsOuvertes = sessionsDuJour.stream()
                .filter(s -> "OUVERTE".equalsIgnoreCase(s.getStatut()))
                .count();
        long nbSessionsFermees = sessionsDuJour.stream()
                .filter(s -> "FERMEE".equalsIgnoreCase(s.getStatut()))
                .count();

        List<Map<String, Object>> detailSessions = sessionsDuJour.stream().map(s -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idSession", s.getIdSessionCaisse());
            item.put("utilisateur", s.getUtilisateur() != null ? s.getUtilisateur().getNom() : null);
            item.put("dateOuverture", s.getDateOuverture());
            item.put("dateFermeture", s.getDateFermeture());
            item.put("soldeOuverture", s.getSoldeOuverture());
            item.put("soldeTheoriqueFermeture", s.getSoldeTheoriqueFermeture());
            item.put("soldePhysiqueFermeture", s.getSoldePhysiqueFermeture());
            item.put("ecart", s.getEcart());
            item.put("statut", s.getStatut());
            return item;
        }).toList();

        Map<String, Object> arrete = new LinkedHashMap<>();
        arrete.put("idCaisse", caisse.getIdCaisse());
        arrete.put("codeCaisse", caisse.getCodeCaisse());
        arrete.put("libelle", caisse.getLibelle());
        arrete.put("soldeTheorique", caisse.getSoldeTheorique());
        arrete.put("dateArrete", date);
        arrete.put("totalSoldeTheorique", totalSoldeTheorique);
        arrete.put("totalSoldePhysique", totalSoldePhysique);
        arrete.put("totalEcart", totalEcart);
        arrete.put("nbSessions", sessionsDuJour.size());
        arrete.put("nbSessionsOuvertes", nbSessionsOuvertes);
        arrete.put("nbSessionsFermees", nbSessionsFermees);
        arrete.put("sessions", detailSessions);
        return arrete;
    }
}
