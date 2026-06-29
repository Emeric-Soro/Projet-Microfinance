package com.soutra.microfinance.service.credit;

import com.soutra.microfinance.dto.request.credit.GarantieRequestDTO;
import com.soutra.microfinance.entity.Credit;
import com.soutra.microfinance.entity.DemandeCredit;
import com.soutra.microfinance.entity.Echeance;
import com.soutra.microfinance.entity.Garantie;
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
	Credit enregistrerRemboursement(Long idCredit, BigDecimal montant, String numCompteSource);

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

	// Consulte les demandes de credit d'un client.
	Page<DemandeCredit> consulterDemandesClient(Long idClient, Pageable pageable);

	// Liste tous les credits de maniere paginee.
	Page<Credit> consulterTousLesCredits(Pageable pageable);

	// Liste les credits par statut.
	Page<Credit> consulterCreditsParStatut(String codeStatut, Pageable pageable);

	// Met un credit en instruction.
	Credit instruireCredit(Long idCredit);

	// Approuve un credit instruit.
	Credit approuverCredit(Long idCredit);

	// Ajoute des garanties a un credit.
	List<Garantie> ajouterGaranties(Long idCredit, List<GarantieRequestDTO> garanties);

	// Restructure un credit (duree, taux).
	Credit restructurerCredit(Long idCredit, Integer nouvelleDureeMois, BigDecimal nouveauTaux);

	// Consulte les echeances en retard.
	List<Echeance> consulterEcheancesRetard();

	// Passe un credit en souffrance.
	Credit passerEnSouffrance(Long idCredit);
}
