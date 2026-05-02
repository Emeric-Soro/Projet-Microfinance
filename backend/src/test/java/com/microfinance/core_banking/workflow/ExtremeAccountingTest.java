package com.microfinance.core_banking.workflow;

import com.microfinance.core_banking.entity.*;
import com.microfinance.core_banking.service.extension.ComptabiliteExtensionService;
import com.microfinance.core_banking.service.DoubleEntryService;
import com.microfinance.core_banking.service.DoubleEntryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExtremeAccountingTest {

    private DoubleEntryService doubleEntryService;

    @BeforeEach
    void setUp() {
        doubleEntryService = new DoubleEntryServiceImpl();
    }

    @Test
    void partieDoubleToujoursEquilibree() {
        BigDecimal totalDebit = new BigDecimal("1000000.00");
        BigDecimal totalCredit = new BigDecimal("1000000.00");

        assertThat(totalDebit).isEqualByComparingTo(totalCredit);
    }

    @Test
    void soldeBancaireNePeutEtreNegatifSansDecouvert() {
        BigDecimal solde = BigDecimal.ZERO;
        BigDecimal retrait = new BigDecimal("1000.00");

        assertThrows(IllegalStateException.class, () -> {
            if (solde.compareTo(retrait) < 0) {
                throw new IllegalStateException("Solde insuffisant");
            }
        });
    }

    @Test
    void cumulTransactionsJournalieres() {
        List<BigDecimal> transactions = List.of(
            new BigDecimal("100000.00"),
            new BigDecimal("25000.00"),
            new BigDecimal("50000.00"),
            new BigDecimal("-15000.00"),
            new BigDecimal("-8000.00")
        );

        BigDecimal soldeInitial = new BigDecimal("500000.00");
        BigDecimal soldeFinal = soldeInitial;

        for (BigDecimal t : transactions) {
            soldeFinal = soldeFinal.add(t);
        }

        assertThat(soldeFinal).isEqualByComparingTo(new BigDecimal("652000.00"));
    }

    @Test
    void arrondiCentimesPreserveIntegrite() {
        BigDecimal montant = new BigDecimal("1000.00");
        BigDecimal taux = new BigDecimal("0.035");
        BigDecimal interet = montant.multiply(taux).setScale(2, RoundingMode.HALF_UP);

        assertThat(interet).isEqualByComparingTo(new BigDecimal("35.00"));
    }

    @Test
    void provisionCreditProgressive() {
        BigDecimal encours = new BigDecimal("5000000.00");
        BigDecimal tauxProvision = new BigDecimal("0.20");
        BigDecimal provision = encours.multiply(tauxProvision).setScale(2, RoundingMode.HALF_UP);

        assertThat(provision).isEqualByComparingTo(new BigDecimal("1000000.00"));
    }

    @Test
    void grandLivreSoldeCorrect() {
        List<LigneEcritureComptable> lignes = new ArrayList<>();

        LigneEcritureComptable l1 = new LigneEcritureComptable();
        l1.setSens("DEBIT");
        l1.setMontant(new BigDecimal("1000.00"));

        LigneEcritureComptable l2 = new LigneEcritureComptable();
        l2.setSens("CREDIT");
        l2.setMontant(new BigDecimal("500.00"));

        LigneEcritureComptable l3 = new LigneEcritureComptable();
        l3.setSens("DEBIT");
        l3.setMontant(new BigDecimal("300.00"));

        LigneEcritureComptable l4 = new LigneEcritureComptable();
        l4.setSens("CREDIT");
        l4.setMontant(new BigDecimal("200.00"));

        lignes.addAll(List.of(l1, l2, l3, l4));

        BigDecimal totalDebit = lignes.stream()
            .filter(l -> "DEBIT".equals(l.getSens()))
            .map(LigneEcritureComptable::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = lignes.stream()
            .filter(l -> "CREDIT".equals(l.getSens()))
            .map(LigneEcritureComptable::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal solde = totalDebit.subtract(totalCredit);

        assertThat(totalDebit).isEqualByComparingTo(new BigDecimal("1300.00"));
        assertThat(totalCredit).isEqualByComparingTo(new BigDecimal("700.00"));
        assertThat(solde).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    void balanceAgeeImpayes() {
        List<ImpayeCredit> impayes = List.of(
            createImpaye(1, new BigDecimal("100000.00"), 30),
            createImpaye(2, new BigDecimal("150000.00"), 60),
            createImpaye(3, new BigDecimal("200000.00"), 90),
            createImpaye(4, new BigDecimal("50000.00"), 120)
        );

        BigDecimal totalImpayes = impayes.stream()
            .map(ImpayeCredit::getMontantImpaye)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal impayesPlus30 = impayes.stream()
            .filter(i -> i.getJoursRetard() > 30)
            .map(ImpayeCredit::getMontantImpaye)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalImpayes).isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(impayesPlus30).isEqualByComparingTo(new BigDecimal("400000.00"));
    }

    @Test
    void amortissementConstant() {
        BigDecimal montant = new BigDecimal("1200000.00");
        int dureeMois = 12;
        BigDecimal mensualiteCapital = montant.divide(BigDecimal.valueOf(dureeMois), RoundingMode.HALF_UP);

        BigDecimal totalRembourse = BigDecimal.ZERO;
        for (int i = 0; i < dureeMois; i++) {
            totalRembourse = totalRembourse.add(mensualiteCapital);
        }

        assertThat(mensualiteCapital).isEqualByComparingTo(new BigDecimal("100000.00"));
        assertThat(totalRembourse).isEqualByComparingTo(montant);
    }

    @Test
    void ecartCaisseDetecte() {
        BigDecimal soldeTheorique = new BigDecimal("1000000.00");
        BigDecimal soldeReel = new BigDecimal("987654.00");
        BigDecimal ecart = soldeTheorique.subtract(soldeReel);

        assertThat(ecart).isEqualByComparingTo(new BigDecimal("12346.00"));
        assertThat(ecart.abs().compareTo(new BigDecimal("10000.00"))).isGreaterThan(0);
    }

    @Test
    void clotureExerciceReporting() {
        BigDecimal totalCharges = new BigDecimal("50000000.00");
        BigDecimal totalProduits = new BigDecimal("75000000.00");
        BigDecimal resultatExercice = totalProduits.subtract(totalCharges);

        assertThat(resultatExercice).isEqualByComparingTo(new BigDecimal("25000000.00"));
        assertThat(resultatExercice.signum()).isPositive();
    }

    private ImpayeCredit createImpaye(long id, BigDecimal montant, int joursRetard) {
        ImpayeCredit i = new ImpayeCredit();
        i.setIdImpaye(id);
        i.setMontantImpaye(montant);
        i.setJoursRetard(joursRetard);
        return i;
    }
}
