package com.soutra.microfinance.api.exception;

import java.math.BigDecimal;

public class SoldeInsuffisantException extends RuntimeException {

    private final BigDecimal fondsDisponibles;
    private final BigDecimal montantRequis;

    public SoldeInsuffisantException(String message, BigDecimal fondsDisponibles, BigDecimal montantRequis) {
        super(message);
        this.fondsDisponibles = fondsDisponibles;
        this.montantRequis = montantRequis;
    }

    public BigDecimal getFondsDisponibles() {
        return fondsDisponibles;
    }

    public BigDecimal getMontantRequis() {
        return montantRequis;
    }
}
