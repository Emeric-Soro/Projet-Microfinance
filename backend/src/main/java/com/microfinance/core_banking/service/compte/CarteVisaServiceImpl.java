package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.repository.compte.CarteVisaRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;

@Service
public class CarteVisaServiceImpl implements CarteVisaService {

    private final CarteVisaRepository carteVisaRepository;
    private final CompteRepository compteRepository;

    // Remplacement obligatoire par SecureRandom pour la cryptographie financière
    private final SecureRandom secureRandom = new SecureRandom();

    public CarteVisaServiceImpl(CarteVisaRepository carteVisaRepository, CompteRepository compteRepository) {
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
        carte.setCvv(genererCvv()); // Ajout du CVV
        carte.setDateExpiration(LocalDate.now().plusYears(3));
        carte.setStatut(Boolean.TRUE);
        carte.setPlafondJournalier(new BigDecimal("500000.00")); // Plafond standard par défaut

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

    private String genererCvv() {
        // Génère un nombre à 3 chiffres (entre 100 et 999)
        int cvv = 100 + secureRandom.nextInt(900);
        return String.valueOf(cvv);
    }
}