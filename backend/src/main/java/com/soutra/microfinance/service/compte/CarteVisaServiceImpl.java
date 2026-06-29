package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.dto.request.compte.CarteVisaPatchRequestDTO;
import com.soutra.microfinance.entity.CarteVisa;
import com.soutra.microfinance.entity.Compte;
import com.soutra.microfinance.repository.compte.CarteVisaRepository;
import com.soutra.microfinance.repository.compte.CompteRepository;
import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import com.soutra.microfinance.audit.AuditContext;

@Service
public class CarteVisaServiceImpl implements CarteVisaService {

    @Value("${app.carte.plafond-journalier-defaut}")
    private BigDecimal plafondDefaut;

    @Value("${app.carte.validite-annees}")
    private int validiteAnnees;

    private final CarteVisaRepository carteVisaRepository;
    private final CompteRepository compteRepository;

    // Remplacement obligatoire par SecureRandom pour la cryptographie financière
    private final SecureRandom secureRandom = new SecureRandom();

    public CarteVisaServiceImpl(
            CarteVisaRepository carteVisaRepository,
            CompteRepository compteRepository
    ) {
        this.carteVisaRepository = carteVisaRepository;
        this.compteRepository = compteRepository;
    }

    @Override
    @Transactional
    public CarteVisa commanderCarte(String numCompte) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));

        CarteVisa carte = new CarteVisa();
        carte.setCompte(compte);
        carte.setNumeroCarte(genererNumeroCarteUnique());
        carte.setDateExpiration(LocalDate.now().plusYears(validiteAnnees));
        carte.setStatut(Boolean.TRUE);
        carte.setPlafondJournalier(plafondDefaut);
        // Le CVV n'est volontairement ni genere ni persiste dans ce domaine applicatif.

        return carteVisaRepository.save(carte);
    }

    @Override
    @Transactional
    public CarteVisa faireOpposition(String numeroCarte) {
        CarteVisa carteVisa = carteVisaRepository.findByNumeroCarte(numeroCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + numeroCarte));

        AuditContext.setIdEntite(numeroCarte);
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("statut", carteVisa.getStatut());
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        // Règle métier : On désactive la carte de manière irréversible
        carteVisa.setStatut(Boolean.FALSE);
        CarteVisa saved = carteVisaRepository.save(carteVisa);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("statut", Boolean.FALSE);
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarteVisa> listerCartesParCompte(String numCompte, Pageable pageable) {
        Compte compte = compteRepository.findByNumCompte(numCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + numCompte));
        return carteVisaRepository.findByCompte_IdCompte(compte.getIdCompte(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CarteVisa obtenirCarte(String numeroCarte) {
        return carteVisaRepository.findByNumeroCarte(numeroCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + numeroCarte));
    }

    @Override
    @Transactional
    public CarteVisa modifierPartiellement(String numeroCarte, CarteVisaPatchRequestDTO patch) {
        CarteVisa carte = carteVisaRepository.findByNumeroCarte(numeroCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + numeroCarte));

        AuditContext.setIdEntite(numeroCarte);
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("plafondJournalier", carte.getPlafondJournalier());
        avant.put("statut", carte.getStatut());
        avant.put("dateExpiration", carte.getDateExpiration());
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        if (patch.getPlafondJournalier() != null) {
            carte.setPlafondJournalier(patch.getPlafondJournalier());
        }
        if (patch.getStatut() != null) {
            carte.setStatut(patch.getStatut());
        }
        if (patch.getDateExpiration() != null) {
            carte.setDateExpiration(patch.getDateExpiration());
        }
        CarteVisa saved = carteVisaRepository.save(carte);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("plafondJournalier", saved.getPlafondJournalier());
        apres.put("statut", saved.getStatut());
        apres.put("dateExpiration", saved.getDateExpiration());
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CarteVisa> listerToutesLesCartes(Pageable pageable) {
        return carteVisaRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CarteVisa faireOppositionParId(Long idCarte) {
        CarteVisa carteVisa = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));

        AuditContext.setIdEntite(carteVisa.getNumeroCarte());
        java.util.Map<String, Object> avant = new java.util.HashMap<>();
        avant.put("statut", carteVisa.getStatut());
        AuditContext.setDetailsAvant(AuditContext.toJson(avant));

        carteVisa.setStatut(Boolean.FALSE);
        CarteVisa saved = carteVisaRepository.save(carteVisa);

        java.util.Map<String, Object> apres = new java.util.HashMap<>();
        apres.put("statut", Boolean.FALSE);
        AuditContext.setDetailsApres(AuditContext.toJson(apres));

        return saved;
    }

    // --- MÉTHODES UTILITAIRES PRIVÉES ---

    private String genererNumeroCarteUnique() {
        String numero;
        do {
            StringBuilder sb = new StringBuilder();
            // Le standard international : Une carte Visa commence toujours par '4'
            sb.append("4");

            // On génère les 15 autres chiffres (pour arriver à 16)
            for (int i = 0; i < 15; i++) {
                sb.append(secureRandom.nextInt(10));
            }
            numero = sb.toString();
        } while (carteVisaRepository.existsByNumeroCarte(numero));
        return numero;
    }

}
