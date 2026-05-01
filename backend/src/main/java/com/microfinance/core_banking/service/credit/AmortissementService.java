package com.microfinance.core_banking.service.credit;

import com.microfinance.core_banking.entity.Echeance;
import com.microfinance.core_banking.entity.MethodeCalculInteret;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
// Service utilitaire dedie au calcul du tableau d'amortissement.
// Supporte les methodes degressif (annuites constantes) et constant (flat rate).
public class AmortissementService {

	private static final MathContext MC = MathContext.DECIMAL128;
	private static final int SCALE = 2;

	// Genere le tableau d'amortissement complet selon la methode de calcul choisie.
	public List<Echeance> genererTableau(
			BigDecimal montant,
			BigDecimal tauxAnnuel,
			int dureeMois,
			MethodeCalculInteret methode,
			LocalDate dateDebut
	) {
		return switch (methode) {
			case DEGRESSIF -> genererTableauDegressif(montant, tauxAnnuel, dureeMois, dateDebut);
			case CONSTANT -> genererTableauConstant(montant, tauxAnnuel, dureeMois, dateDebut);
			case IN_FINE -> genererTableauInFine(montant, tauxAnnuel, dureeMois, dateDebut);
		};
	}

	// Methode degressive : annuites constantes, interets decroissants.
	// Formule : Mensualite = M * (t / (1 - (1+t)^-n))
	// Ou M = montant, t = taux mensuel, n = nombre de mois.
	private List<Echeance> genererTableauDegressif(
			BigDecimal montant,
			BigDecimal tauxAnnuel,
			int dureeMois,
			LocalDate dateDebut
	) {
		List<Echeance> echeances = new ArrayList<>();
		BigDecimal tauxMensuel = tauxAnnuel.divide(BigDecimal.valueOf(12 * 100), MC);

		// Calcul de la mensualite constante
		// mensualite = montant * tauxMensuel / (1 - (1 + tauxMensuel)^(-dureeMois))
		double tM = tauxMensuel.doubleValue();
		double mensualiteDouble = montant.doubleValue() * tM / (1 - Math.pow(1 + tM, -dureeMois));
		BigDecimal mensualite = BigDecimal.valueOf(mensualiteDouble).setScale(SCALE, RoundingMode.HALF_UP);

		BigDecimal capitalRestant = montant;

		for (int i = 1; i <= dureeMois; i++) {
			BigDecimal interet = capitalRestant.multiply(tauxMensuel).setScale(SCALE, RoundingMode.HALF_UP);
			BigDecimal capital;

			if (i == dureeMois) {
				// Derniere echeance : on solde le capital restant pour eviter les arrondis
				capital = capitalRestant;
			} else {
				capital = mensualite.subtract(interet);
			}

			BigDecimal total = capital.add(interet);
			capitalRestant = capitalRestant.subtract(capital);

			Echeance echeance = creerEcheance(i, dateDebut.plusMonths(i), capital, interet, total);
			echeances.add(echeance);
		}

		return echeances;
	}

	// Methode constante (flat rate) : interets calcules sur le montant initial.
	// Plus simple mais plus couteux pour l'emprunteur.
	private List<Echeance> genererTableauConstant(
			BigDecimal montant,
			BigDecimal tauxAnnuel,
			int dureeMois,
			LocalDate dateDebut
	) {
		List<Echeance> echeances = new ArrayList<>();

		// Interet total = montant * taux annuel / 100 * (duree en annees)
		BigDecimal interetTotal = montant
				.multiply(tauxAnnuel)
				.multiply(BigDecimal.valueOf(dureeMois))
				.divide(BigDecimal.valueOf(12 * 100), SCALE, RoundingMode.HALF_UP);

		BigDecimal interetMensuel = interetTotal.divide(BigDecimal.valueOf(dureeMois), SCALE, RoundingMode.HALF_UP);
		BigDecimal capitalMensuel = montant.divide(BigDecimal.valueOf(dureeMois), SCALE, RoundingMode.HALF_UP);

		BigDecimal capitalRestant = montant;

		for (int i = 1; i <= dureeMois; i++) {
			BigDecimal capital;
			if (i == dureeMois) {
				capital = capitalRestant;
			} else {
				capital = capitalMensuel;
			}

			BigDecimal total = capital.add(interetMensuel);
			capitalRestant = capitalRestant.subtract(capital);

			Echeance echeance = creerEcheance(i, dateDebut.plusMonths(i), capital, interetMensuel, total);
			echeances.add(echeance);
		}

		return echeances;
	}

	// Methode in fine : seuls les interets sont payes periodiquement,
	// le capital est rembourse en une seule fois a la derniere echeance.
	private List<Echeance> genererTableauInFine(
			BigDecimal montant,
			BigDecimal tauxAnnuel,
			int dureeMois,
			LocalDate dateDebut
	) {
		List<Echeance> echeances = new ArrayList<>();
		BigDecimal interetMensuel = montant
				.multiply(tauxAnnuel)
				.divide(BigDecimal.valueOf(12 * 100), SCALE, RoundingMode.HALF_UP);

		for (int i = 1; i <= dureeMois; i++) {
			BigDecimal capital = (i == dureeMois) ? montant : BigDecimal.ZERO;
			BigDecimal total = capital.add(interetMensuel);

			Echeance echeance = creerEcheance(i, dateDebut.plusMonths(i), capital, interetMensuel, total);
			echeances.add(echeance);
		}

		return echeances;
	}

	// Fabrique une echeance avec les valeurs calculees.
	private Echeance creerEcheance(int numero, LocalDate dateEcheance,
									BigDecimal capital, BigDecimal interet, BigDecimal total) {
		Echeance echeance = new Echeance();
		echeance.setNumeroEcheance(numero);
		echeance.setDateEcheance(dateEcheance);
		echeance.setMontantCapital(capital);
		echeance.setMontantInteret(interet);
		echeance.setMontantTotal(total);
		echeance.setMontantPenalite(BigDecimal.ZERO);
		echeance.setMontantPaye(BigDecimal.ZERO);
		echeance.setEstPayee(false);
		return echeance;
	}
}
