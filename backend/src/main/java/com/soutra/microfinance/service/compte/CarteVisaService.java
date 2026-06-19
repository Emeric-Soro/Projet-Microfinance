package com.soutra.microfinance.service.compte;

import com.soutra.microfinance.dto.request.compte.CarteVisaPatchRequestDTO;
import com.soutra.microfinance.entity.CarteVisa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarteVisaService {

    CarteVisa commanderCarte(String numCompte);

    CarteVisa faireOpposition(String numeroCarte);

    Page<CarteVisa> listerCartesParCompte(String numCompte, Pageable pageable);

    CarteVisa obtenirCarte(String numeroCarte);

    CarteVisa modifierPartiellement(String numeroCarte, CarteVisaPatchRequestDTO patch);

    Page<CarteVisa> listerToutesLesCartes(Pageable pageable);

    CarteVisa faireOppositionParId(Long idCarte);
}
