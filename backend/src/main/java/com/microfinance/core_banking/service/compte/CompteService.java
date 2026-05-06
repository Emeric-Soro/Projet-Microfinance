package com.microfinance.core_banking.service.compte;

import com.microfinance.core_banking.entity.Compte;

import java.math.BigDecimal;

public interface CompteService {

    Compte ouvrirCompte(Long idClient, String codeTypeCompte, BigDecimal depotInitial);

    BigDecimal consulterSolde(String numCompte);

    Compte changerDecouvertAutorise(String numCompte, BigDecimal nouveauPlafond);

    Compte cloturerCompte(String numCompte);

    Compte bloquerCompte(String numCompte, String motif);

    Compte debloquerCompte(String numCompte, String motif);
}
