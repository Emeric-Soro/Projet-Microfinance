package com.soutra.microfinance.service.tarification;

import com.soutra.microfinance.entity.Agio;

import java.util.List;
import java.util.Optional;

public interface AgioService {

    List<Agio> calculerFraisTenueCompteMensuel();

    Optional<Agio> calculerPenaliteDecouvert(String numCompte);

    List<Agio> executerPrelevementsEnAttente(Long idUserSysteme);
}
