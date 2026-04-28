package com.microfinance.core_banking.service.operation.fees;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TransactionFeeCalculator {

    private final Map<String, TransactionFeeStrategy> strategyByCode;

    public TransactionFeeCalculator(List<TransactionFeeStrategy> strategies) {
        this.strategyByCode = strategies.stream()
                .collect(Collectors.toMap(TransactionFeeStrategy::codeTypeTransaction, Function.identity()));
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
        return strategy.calculerFrais(montant);
    }
}
