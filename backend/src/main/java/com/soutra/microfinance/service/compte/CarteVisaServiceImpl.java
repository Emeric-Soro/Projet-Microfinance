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

        // Règle métier : On désactive la carte de manière irréversible
        carteVisa.setStatut(Boolean.FALSE);
        return carteVisaRepository.save(carteVisa);
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

        if (patch.getPlafondJournalier() != null) {
            carte.setPlafondJournalier(patch.getPlafondJournalier());
        }
        if (patch.getStatut() != null) {
            carte.setStatut(patch.getStatut());
        }
        if (patch.getDateExpiration() != null) {
            carte.setDateExpiration(patch.getDateExpiration());
        }
        return carteVisaRepository.save(carte);
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
