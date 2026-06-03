package com.soutra.microfinance.service.parametrage;

import com.soutra.microfinance.entity.Agence;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Interface du service de gestion des agences.
public interface AgenceService {
	Agence creerAgence(Agence agence);
	Agence modifierAgence(Long idAgence, Agence agence);
	Agence obtenirAgence(Long idAgence);
	List<Agence> listerAgencesActives();

	Page<Agence> listerAgencesPagine(Pageable pageable);
	Agence desactiverAgence(Long idAgence);
}
