package com.microfinance.core_banking.service.tarification;

import com.microfinance.core_banking.entity.Agio;

import java.util.List;
import java.util.Optional;

public interface AgioService {

    List<Agio> calculerFraisTenueCompteMensuel();

    Optional<Agio> calculerPenaliteDecouvert(String numCompte);

    List<Agio> executerPrelevementsEnAttente(Long idUserSysteme);
}
