package com.microfinance.core_banking.service.operation.fees;

import java.math.BigDecimal;

public interface TransactionFeeStrategy {

    String codeTypeTransaction();

    BigDecimal calculerFrais(BigDecimal montant);
}
