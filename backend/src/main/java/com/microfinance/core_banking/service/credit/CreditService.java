package com.microfinance.core_banking.service.credit;

import com.microfinance.core_banking.entity.Credit;
import com.microfinance.core_banking.entity.DemandeCredit;
import com.microfinance.core_banking.entity.Echeance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

// Interface du service metier de gestion des credits.
public interface CreditService {

	// Soumet une nouvelle demande de credit.
	DemandeCredit soumettreDemandeCredit(Long idClient, String codeProduitCredit,
										 BigDecimal montantDemande, Integer dureeSouhaitee,
										 String objetCredit, Long idAgentCredit);

	// Approuve une demande de credit et cree le credit associe.
	Credit approuverDemande(Long idDemande);

	// Rejette une demande de credit avec un motif.
	DemandeCredit rejeterDemande(Long idDemande, String motifRejet);

	// Decaisse un credit approuve (verse les fonds sur le compte du client).
	Credit decaisserCredit(Long idCredit, String numCompteCible);

	// Enregistre un remboursement sur un credit actif.
	Credit enregistrerRemboursement(Long idCredit, BigDecimal montant);

	// Consulte le tableau d'amortissement d'un credit.
	List<Echeance> consulterTableauAmortissement(Long idCredit);

	// Consulte les credits d'un client.
	Page<Credit> consulterCreditsClient(Long idClient, Pageable pageable);

	// Consulte le detail d'un credit.
	Credit consulterCredit(Long idCredit);

	// Liste les demandes en attente de decision.
	Page<DemandeCredit> listerDemandesEnAttente(Pageable pageable);

	// Consulte le detail d'une demande.
	DemandeCredit consulterDemande(Long idDemande);
}
