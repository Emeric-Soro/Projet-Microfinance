package com.soutra.microfinance.service.operation.fees;

import java.math.BigDecimal;

public interface TransactionFeeStrategy {

    String codeTypeTransaction();

    BigDecimal calculerFrais(BigDecimal montant);
}
