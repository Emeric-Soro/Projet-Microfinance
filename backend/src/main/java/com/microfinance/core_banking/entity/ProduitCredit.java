package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produit_credit")
// Entite representant un produit de credit configurable (ex: Micro-credit Commerce, Pret Salarie).
public class ProduitCredit extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_produit_credit")
	// Identifiant unique du produit de credit.
	private Long idProduitCredit;

	@Column(name = "code_produit", nullable = false, length = 50, unique = true)
	// Code unique du produit (ex: MC-COMMERCE).
	private String codeProduit;

	@Column(nullable = false, length = 150)
	// Libelle du produit de credit.
	private String libelle;

	@Column(name = "taux_interet_annuel", nullable = false, precision = 7, scale = 4)
	// Taux d'interet annuel par defaut du produit.
	private BigDecimal tauxInteretAnnuel;

	@Column(name = "duree_min_mois", nullable = false)
	// Duree minimale du pret en mois.
	private Integer dureeMinMois;

	@Column(name = "duree_max_mois", nullable = false)
	// Duree maximale du pret en mois.
	private Integer dureeMaxMois;

	@Column(name = "montant_min", nullable = false, precision = 19, scale = 2)
	// Montant minimum empruntable.
	private BigDecimal montantMin;

	@Column(name = "montant_max", nullable = false, precision = 19, scale = 2)
	// Montant maximum empruntable.
	private BigDecimal montantMax;

	@Enumerated(EnumType.STRING)
	@Column(name = "methode_calcul", nullable = false, length = 20)
	// Methode de calcul des interets par defaut.
	private MethodeCalculInteret methodeCalcul;

	@Column(name = "frais_dossier_pourcentage", precision = 7, scale = 4)
	// Pourcentage de frais de dossier applique au montant du pret.
	private BigDecimal fraisDossierPourcentage;

	@Column(name = "penalite_retard_pourcentage", precision = 7, scale = 4)
	// Pourcentage de penalite applique par jour de retard.
	private BigDecimal penaliteRetardPourcentage;

	@Column(name = "est_actif", nullable = false)
	// Indique si le produit est actuellement commercialise.
	private Boolean estActif = true;

	@OneToMany(mappedBy = "produitCredit")
	// Credits bases sur ce produit.
	private List<Credit> credits = new ArrayList<>();

	@OneToMany(mappedBy = "produitCredit")
	// Demandes de credit liees a ce produit.
	private List<DemandeCredit> demandes = new ArrayList<>();
}
