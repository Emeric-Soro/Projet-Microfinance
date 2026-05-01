package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demande_credit")
// Entite representant une demande de pret en phase d'instruction.
public class DemandeCredit extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_demande")
	// Identifiant unique de la demande.
	private Long idDemande;

	@Column(name = "reference_demande", nullable = false, length = 80, unique = true)
	// Reference unique metier de la demande (ex: DEM-20260501-XXXX).
	private String referenceDemande;

	@Column(name = "montant_demande", nullable = false, precision = 19, scale = 2)
	// Montant souhaite par le client.
	private BigDecimal montantDemande;

	@Column(name = "duree_souhaitee", nullable = false)
	// Duree souhaitee du pret en mois.
	private Integer dureeSouhaitee;

	@Column(name = "objet_credit", nullable = false, length = 500)
	// Objet ou motif du credit (ex: achat de marchandises).
	private String objetCredit;

	@Column(name = "date_demande", nullable = false)
	// Date de soumission de la demande.
	private LocalDate dateDemande;

	@Column(name = "date_decision")
	// Date de decision (approbation ou rejet).
	private LocalDateTime dateDecision;

	@Column(name = "motif_rejet", length = 500)
	// Motif du rejet si la demande est refusee.
	private String motifRejet;

	@Column(name = "score_client")
	// Score de risque calcule pour le client au moment de la demande.
	private Integer scoreClient;

	@Enumerated(EnumType.STRING)
	@Column(name = "statut_demande", nullable = false, length = 20)
	// Statut courant de la demande dans le workflow.
	private StatutDemande statutDemande;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_client", nullable = false)
	// Client demandeur du credit.
	private Client client;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_produit_credit", nullable = false)
	// Produit de credit choisi pour cette demande.
	private ProduitCredit produitCredit;

	@ManyToOne
	@JoinColumn(name = "id_agent_credit")
	// Agent de credit en charge du dossier.
	private Utilisateur agentCredit;

	@OneToOne(mappedBy = "demandeCredit")
	// Credit genere si la demande est approuvee et decaissee.
	private Credit credit;
}
