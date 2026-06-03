package com.soutra.microfinance.api.exception;

import jakarta.persistence.EntityNotFoundException;

public class CreditNotFoundException extends EntityNotFoundException {

    public CreditNotFoundException(Long idCredit) {
        super("Credit introuvable : " + idCredit);
    }

    public CreditNotFoundException(String reference) {
        super("Credit introuvable : " + reference);
    }
}
