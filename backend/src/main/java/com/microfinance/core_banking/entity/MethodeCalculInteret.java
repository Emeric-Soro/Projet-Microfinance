package com.microfinance.core_banking.entity;

// Methodes de calcul des interets sur un credit.
public enum MethodeCalculInteret {
	// Annuites constantes, interets decroissants (standard microfinance).
	DEGRESSIF,
	// Interets calcules sur le montant initial (flat rate).
	CONSTANT,
	// Capital rembourse en une seule fois a l'echeance, interets periodiques.
	IN_FINE
}
