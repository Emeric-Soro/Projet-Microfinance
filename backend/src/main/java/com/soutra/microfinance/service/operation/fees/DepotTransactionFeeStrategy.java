package com.soutra.microfinance.service.operation.fees;

import com.soutra.microfinance.constant.AppConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepotTransactionFeeStrategy implements TransactionFeeStrategy {

    @Override
    public String codeTypeTransaction() {
        return AppConstants.TX_DEPOT;
    }

    @Override
    public BigDecimal calculerFrais(BigDecimal montant) {
        return BigDecimal.ZERO;
    }
}
