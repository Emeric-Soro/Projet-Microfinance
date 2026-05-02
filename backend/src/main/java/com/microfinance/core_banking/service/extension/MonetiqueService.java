package com.microfinance.core_banking.service.extension;

import com.microfinance.core_banking.entity.CarteVisa;
import com.microfinance.core_banking.entity.Compte;
import com.microfinance.core_banking.repository.compte.CarteVisaRepository;
import com.microfinance.core_banking.repository.compte.CompteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MonetiqueService {

    private final CarteVisaRepository carteVisaRepository;
    private final CompteRepository compteRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public MonetiqueService(CarteVisaRepository carteVisaRepository,
                            CompteRepository compteRepository,
                            PasswordEncoder passwordEncoder) {
        this.carteVisaRepository = carteVisaRepository;
        this.compteRepository = compteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CarteVisa emettreCarte(Long idCompte, String typeCarte, BigDecimal plafondJournalier, BigDecimal plafondMensuel) {
        Compte compte = compteRepository.findById(idCompte)
                .orElseThrow(() -> new EntityNotFoundException("Compte introuvable: " + idCompte));

        CarteVisa carte = new CarteVisa();
        carte.setCompte(compte);
        carte.setNumeroCarte(genererNumeroCarte());
        carte.setDateExpiration(LocalDate.now().plusYears(3));
        carte.setStatut(true);
        carte.setPlafondJournalier(plafondJournalier != null ? plafondJournalier : new BigDecimal("500000"));

        carte.setTypeCarte(typeCarte != null ? typeCarte : "DEBIT");
        carte.setPlafondMensuel(plafondMensuel);
        carte.setSoldePrepaye("PREPAID".equalsIgnoreCase(typeCarte) ? plafondMensuel : BigDecimal.ZERO);
        carte.setBloque(false);
        carte.setTentativePin(0);

        return carteVisaRepository.save(carte);
    }

    @Transactional
    public void definirPin(Long idCarte, String pin) {
        CarteVisa carte = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));
        if (pin == null || pin.length() < 4 || !pin.matches("\\d{4,6}")) {
            throw new IllegalArgumentException("Le PIN doit contenir entre 4 et 6 chiffres");
        }
        carte.setPinHash(passwordEncoder.encode(pin));
        carte.setTentativePin(0);
        carteVisaRepository.save(carte);
    }

    @Transactional(readOnly = true)
    public boolean verifierPin(Long idCarte, String pin) {
        CarteVisa carte = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));
        if (carte.getBloque()) return false;
        if (carte.getPinHash() == null) return false;

        boolean valide = passwordEncoder.matches(pin, carte.getPinHash());
        if (!valide) {
            carte.setTentativePin(carte.getTentativePin() + 1);
            if (carte.getTentativePin() >= 3) {
                carte.setBloque(true);
            }
            carteVisaRepository.save(carte);
        } else {
            carte.setTentativePin(0);
            carte.setDateDerniereUtilisation(LocalDateTime.now());
            carteVisaRepository.save(carte);
        }
        return valide;
    }

    @Transactional
    public CarteVisa bloquerCarte(Long idCarte, String motif) {
        CarteVisa carte = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));
        carte.setBloque(true);
        return carteVisaRepository.save(carte);
    }

    @Transactional
    public CarteVisa debloquerCarte(Long idCarte) {
        CarteVisa carte = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));
        carte.setBloque(false);
        carte.setTentativePin(0);
        return carteVisaRepository.save(carte);
    }

    public String tokeniserCarte(Long idCarte) {
        CarteVisa carte = carteVisaRepository.findById(idCarte)
                .orElseThrow(() -> new EntityNotFoundException("Carte introuvable: " + idCarte));
        String token = "TOK-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        carte.setTokenCarte(token);
        carteVisaRepository.save(carte);
        return token;
    }

    private String genererNumeroCarte() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
