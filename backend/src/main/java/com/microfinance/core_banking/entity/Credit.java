package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit")
// Entite representant un pret actif apres decaissement.
public class Credit extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_credit")
	// Identifiant unique du credit.
	private Long idCredit;

	@Column(name = "reference_credit", nullable = false, length = 80, unique = true)
	// Reference unique metier du credit (ex: CRD-20260501-XXXX).
	private String referenceCredit;

	@Column(name = "montant_accorde", nullable = false, precision = 19, scale = 2)
	// Montant total accorde au client.
	private BigDecimal montantAccorde;

	@Column(name = "montant_restant_du", nullable = false, precision = 19, scale = 2)
	// Capital restant du (diminue a chaque remboursement).
	private BigDecimal montantRestantDu;

	@Column(name = "taux_interet_annuel", nullable = false, precision = 7, scale = 4)
	// Taux d'interet annuel applique au credit.
	private BigDecimal tauxInteretAnnuel;

	@Column(name = "duree_mois", nullable = false)
	// Duree totale du credit en mois.
	private Integer dureeMois;

	@Enumerated(EnumType.STRING)
	@Column(name = "methode_calcul", nullable = false, length = 20)
	// Methode de calcul des interets utilisee.
	private MethodeCalculInteret methodeCalcul;

	@Column(name = "frais_dossier", precision = 19, scale = 2)
	// Montant des frais de dossier preleves.
	private BigDecimal fraisDossier;

	@Column(name = "date_decaissement")
	// Date de mise a disposition des fonds.
	private LocalDate dateDecaissement;

	@Column(name = "date_fin_prevue")
	// Date de fin prevue du remboursement.
	private LocalDate dateFinPrevue;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_client", nullable = false)
	// Client emprunteur.
	private Client client;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_produit_credit", nullable = false)
	// Produit de credit sur lequel est base le pret.
	private ProduitCredit produitCredit;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_statut_credit", nullable = false)
	// Statut courant du credit.
	private StatutCredit statutCredit;

	@ManyToOne
	@JoinColumn(name = "id_compte_decaissement")
	// Compte du client sur lequel les fonds sont verses.
	private Compte compteDecaissement;

	@OneToOne
	@JoinColumn(name = "id_demande", unique = true)
	// Demande de credit a l'origine de ce pret.
	private DemandeCredit demandeCredit;

	@OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
	// Echeances composant le tableau d'amortissement.
	private List<Echeance> echeances = new ArrayList<>();

	@OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
	// Garanties associees au credit.
	private List<Garantie> garanties = new ArrayList<>();
}
