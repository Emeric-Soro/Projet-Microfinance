package com.soutra.microfinance.api.exception;

import jakarta.persistence.EntityNotFoundException;

public class CompteNotFoundException extends EntityNotFoundException {

    public CompteNotFoundException(String numCompte) {
        super("Compte introuvable : " + numCompte);
    }
}
