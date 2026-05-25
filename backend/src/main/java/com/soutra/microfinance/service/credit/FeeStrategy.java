package com.soutra.microfinance.service.credit;

import com.soutra.microfinance.api.exception.UsuryRateExceededException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Service de calcul du TAEG (Taux Annuel Effectif Global) et validation du plafond d'usure BCEAO.
 * Utilise la methode de Newton-Raphson pour resoudre l'equation d'amortissement.
 * Conformement a la reglementation UEMOA/BCEAO, le taux ne peut exceder 24% annuel.
 */
@Service
public class FeeStrategy {

    // Plafond d'usure BCEAO : 24% annuel
    private static final double PLAFOND_USURE_BCEAO = 0.24;

    // Precision et limites de la methode de Newton-Raphson
    private static final int MAX_ITERATIONS = 100;
    private static final double CONVERGENCE_THRESHOLD = 1e-10;
    private static final double INITIAL_GUESS = 0.02; // Estimation initiale mensuelle ~2%

    // Contexte de precision pour les calculs BigDecimal
    private static final MathContext MC = MathContext.DECIMAL128;

    /**
     * Calcule le TAEG a partir des parametres du pret et valide le plafond d'usure.
     *
     * @param montantPrincipal    montant du pret accorde
     * @param montantMensualite   montant de la mensualite (fixe ou premiere mensualite)
     * @param nombreMensualites   nombre total de mensualites
     * @return le TAEG calcule (taux annuel decimal, ex: 0.18 pour 18%)
     * @throws UsuryRateExceededException si le TAEG depasse 24%
     * @throws IllegalArgumentException   si les parametres sont invalides
     */
    public double calculerTAEG(BigDecimal montantPrincipal, BigDecimal montantMensualite, int nombreMensualites) {
        validerParametres(montantPrincipal, montantMensualite, nombreMensualites);

        double principal = montantPrincipal.doubleValue();
        double mensualite = montantMensualite.doubleValue();
        int n = nombreMensualites;

        // Resolution par Newton-Raphson : f(r) = somme[i=1..n](mensualite/(1+r)^i) - principal = 0
        double tauxMensuel = newtonRaphson(principal, mensualite, n);

        // Conversion taux mensuel -> taux annuel effectif : (1 + r)^12 - 1
        double taeg = Math.pow(1 + tauxMensuel, 12) - 1;

        validerPlafondUsure(taeg);
        return taeg;
    }

    /**
     * Calcule le TAEG et retourne le taux sous forme de BigDecimal.
     */
    public BigDecimal calculerTAEGBigDecimal(BigDecimal montantPrincipal, BigDecimal montantMensualite, int nombreMensualites) {
        double taeg = calculerTAEG(montantPrincipal, montantMensualite, nombreMensualites);
        return BigDecimal.valueOf(taeg).setScale(6, RoundingMode.HALF_UP);
    }

    /**
     * Valide que le TAEG ne depasse pas le plafond d'usure BCEAO.
     *
     * @param taeg le TAEG calcule (taux annuel decimal)
     * @throws UsuryRateExceededException si le taux depasse 24%
     */
    public void validerPlafondUsure(double taeg) {
        if (taeg > PLAFOND_USURE_BCEAO) {
            throw new UsuryRateExceededException(taeg, PLAFOND_USURE_BCEAO);
        }
    }

    /**
     * Calcule le cout total du credit (difference entre total des mensualites et principal).
     *
     * @param montantMensualite montant de chaque mensualite
     * @param nombreMensualites nombre de mensualites
     * @param montantPrincipal  montant du pret
     * @return le cout total du credit
     */
    public BigDecimal calculerCoutTotal(BigDecimal montantMensualite, int nombreMensualites, BigDecimal montantPrincipal) {
        BigDecimal totalVerse = montantMensualite.multiply(BigDecimal.valueOf(nombreMensualites));
        return totalVerse.subtract(montantPrincipal);
    }

    /**
     * Retourne le plafond d'usure BCEAO applicable.
     */
    public double getPlafondUsure() {
        return PLAFOND_USURE_BCEAO;
    }

    /**
     * Methode de Newton-Raphson pour resoudre l'equation d'amortissement.
     * f(r) = somme[i=1..n](M/(1+r)^i) - P = 0
     * f'(r) = somme[i=1..n](-i * M / (1+r)^(i+1))
     *
     * @return taux mensuel solution
     */
    private double newtonRaphson(double principal, double mensualite, int n) {
        double r = INITIAL_GUESS;

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            double f = 0.0;
            double fPrime = 0.0;

            for (int i = 1; i <= n; i++) {
                double disc = Math.pow(1 + r, i);
                f += mensualite / disc;
                fPrime += -i * mensualite / (disc * (1 + r));
            }
            f -= principal;

            // Protection contre division par zero
            if (Math.abs(fPrime) < 1e-15) {
                break;
            }

            double delta = f / fPrime;
            r -= delta;

            // Verification de convergence
            if (Math.abs(delta) < CONVERGENCE_THRESHOLD) {
                return r;
            }
        }

        // Si pas de convergence, retourner l'estimation courante
        return r;
    }

    /**
     * Valide que les parametres du pret sont coerents.
     */
    private void validerParametres(BigDecimal montantPrincipal, BigDecimal montantMensualite, int nombreMensualites) {
        if (montantPrincipal == null || montantPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant principal doit etre strictement positif");
        }
        if (montantMensualite == null || montantMensualite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La mensualite doit etre strictement positive");
        }
        if (nombreMensualites <= 0) {
            throw new IllegalArgumentException("Le nombre de mensualites doit etre strictement positif");
        }
        // Verification que le total des mensualites couvre au moins le principal
        BigDecimal totalVerse = montantMensualite.multiply(BigDecimal.valueOf(nombreMensualites));
        if (totalVerse.compareTo(montantPrincipal) < 0) {
            throw new IllegalArgumentException(
                    "Le total des mensualites (" + totalVerse + ") doit etre superieur au principal (" + montantPrincipal + ")");
        }
    }
}
