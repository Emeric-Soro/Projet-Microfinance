package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.CarteVisa;

public interface CarteVisaService {

    CarteVisa commanderCarte(String numCompte);

    CarteVisa faireOpposition(String numeroCarte);
}
