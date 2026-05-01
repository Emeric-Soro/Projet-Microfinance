package com.microfinance.core_banking.service.parametrage;

import com.microfinance.core_banking.entity.Agence;
import java.util.List;

// Interface du service de gestion des agences.
public interface AgenceService {
	Agence creerAgence(Agence agence);
	Agence modifierAgence(Long idAgence, Agence agence);
	Agence obtenirAgence(Long idAgence);
	List<Agence> listerAgencesActives();
	Agence desactiverAgence(Long idAgence);
}
