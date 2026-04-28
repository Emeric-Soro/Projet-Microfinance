package com.microfinance.core_banking.service.operation.fees;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepotTransactionFeeStrategy implements TransactionFeeStrategy {

    @Override
    public String codeTypeTransaction() {
        return "DEPOT";
    }

    @Override
    public BigDecimal calculerFrais(BigDecimal montant) {
        return BigDecimal.ZERO;
    }
}
