package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.entity.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CompteService {

    Compte ouvrirCompte(Long idClient, String codeTypeCompte, BigDecimal depotInitial, Long idAgence, BigDecimal decouvertAutorise);

    BigDecimal consulterSolde(String numCompte);

    Compte consulterCompte(Long idCompte);

    Page<Compte> listerComptesClient(Long idClient, Pageable pageable);

    Page<Compte> listerTousLesComptes(Pageable pageable);

    Page<Compte> rechercherComptes(String query, Pageable pageable);

    Compte changerDecouvertAutorise(String numCompte, BigDecimal nouveauPlafond);

    Compte cloturerCompte(String numCompte);

    Compte bloquerCompte(String numCompte, String motif);

    Compte debloquerCompte(String numCompte, String motif);

    Compte obtenirCompteParNumero(String numCompte);
}
