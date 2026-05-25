package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.entity.CarteVisa;

public interface CarteVisaService {

    CarteVisa commanderCarte(String numCompte);

    CarteVisa faireOpposition(String numeroCarte);
}
