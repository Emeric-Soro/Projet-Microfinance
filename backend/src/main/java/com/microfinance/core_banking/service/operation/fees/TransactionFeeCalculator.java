package com.microfinance.core_banking.service.operation.fees;

import com.microfinance.core_banking.service.extension.FiscaliteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionFeeCalculator {

    private static final String CODE_TAXE_TRANSACTION = "TAXE_TRANSACTION";

    private final Map<String, TransactionFeeStrategy> strategyByCode;
    private final FiscaliteService fiscaliteService;

    public TransactionFeeCalculator(List<TransactionFeeStrategy> strategies, FiscaliteService fiscaliteService) {
        this.strategyByCode = strategies.stream()
                .collect(Collectors.toMap(TransactionFeeStrategy::codeTypeTransaction, Function.identity()));
        this.fiscaliteService = fiscaliteService;
    }

    TransactionFeeCalculator(List<TransactionFeeStrategy> strategies) {
        this(strategies, null);
    }

    public BigDecimal calculerFrais(String codeTypeTransaction, BigDecimal montant) {
        if (codeTypeTransaction == null || codeTypeTransaction.isBlank()) {
            throw new IllegalArgumentException("Le code type de transaction est obligatoire");
        }
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit etre strictement positif");
        }

        TransactionFeeStrategy strategy = strategyByCode.get(codeTypeTransaction);
        if (strategy == null) {
            throw new IllegalStateException("Aucune strategie de tarification pour le type: " + codeTypeTransaction);
        }
        BigDecimal fraisMetier = strategy.calculerFrais(montant);
        if (fraisMetier == null || fraisMetier.compareTo(BigDecimal.ZERO) <= 0 || fiscaliteService == null) {
            return fraisMetier == null ? BigDecimal.ZERO : fraisMetier;
        }
        BigDecimal taxe = fiscaliteService.calculerTaxe(CODE_TAXE_TRANSACTION, fraisMetier, codeTypeTransaction);
        return fraisMetier.add(taxe == null ? BigDecimal.ZERO : taxe);
    }
}
