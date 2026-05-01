package com.microfinance.core_banking.entity;

// Etats possibles d'une demande de credit dans le workflow d'approbation.
public enum StatutDemande {
	EN_ATTENTE,
	EN_ETUDE,
	APPROUVEE,
	REJETEE
}
