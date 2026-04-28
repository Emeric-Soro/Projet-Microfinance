package com.microfinance.core_banking.service.operation.fees;

import com.microfinance.core_banking.service.tarification.TarificationParametreService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class VirementTransactionFeeStrategy implements TransactionFeeStrategy {

    private static final String PARAM_FEE_RATE = "TRANSACTION_FEE_RATE_VIREMENT";
    private final TarificationParametreService tarificationParametreService;

    public VirementTransactionFeeStrategy(TarificationParametreService tarificationParametreService) {
        this.tarificationParametreService = tarificationParametreService;
    }

    @Override
    public String codeTypeTransaction() {
        return "VIREMENT";
    }

    @Override
    public BigDecimal calculerFrais(BigDecimal montant) {
        BigDecimal taux = tarificationParametreService.lireValeurDecimale(PARAM_FEE_RATE);
        if (taux.compareTo(BigDecimal.ZERO) < 0 || taux.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalStateException("Taux de frais VIREMENT invalide: " + taux);
        }
        return montant.multiply(taux).setScale(2, RoundingMode.HALF_UP);
    }
}
