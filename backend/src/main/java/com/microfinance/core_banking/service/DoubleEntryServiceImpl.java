package com.microfinance.core_banking.service;

import com.microfinance.core_banking.dto.BalanceLine;
import com.microfinance.core_banking.entity.SensEcriture;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of double-entry validation logic extracted from ComptabiliteExtensionService.
 * Validates that a piece has balanced debit and credit lines and that each line is valid.
 */
@Service
public class DoubleEntryServiceImpl implements DoubleEntryService {

    @Override
    public void validerPieceEquilibree(List<BalanceLine> balanceLines) {
        if (balanceLines == null || balanceLines.isEmpty()) {
            throw new IllegalArgumentException("Une piece comptable doit contenir des lignes");
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        int nombreDebits = 0;
        int nombreCredits = 0;

        for (BalanceLine line : balanceLines) {
            String compte = line.numeroCompte();
            if (compte == null || compte.isBlank()) {
                throw new IllegalArgumentException("Chaque ligne comptable doit porter un compte");
            }
            if (line.montant() == null || line.montant().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Chaque ligne comptable doit porter un montant positif");
            }
            if (line.sens() == null) {
                throw new IllegalArgumentException("Le sens de la ligne comptable doit etre precisé");
            }
            if (line.sens() == SensEcriture.DEBIT) {
                totalDebit = totalDebit.add(line.montant());
                nombreDebits++;
            } else {
                totalCredit = totalCredit.add(line.montant());
                nombreCredits++;
            }
        }

        if (nombreDebits == 0 || nombreCredits == 0) {
            throw new IllegalStateException("Une piece comptable doit contenir au moins une ligne debit et une ligne credit");
        }
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalStateException("La piece comptable n'est pas equilibree");
        }
    }
}
