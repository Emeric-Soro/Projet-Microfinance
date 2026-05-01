package com.microfinance.core_banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produit_epargne")
// Entite representant un produit d'epargne configurable (ex: Epargne a vue, DAT 12 mois).
public class ProduitEpargne extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_produit_epargne")
	// Identifiant unique du produit d'epargne.
	private Long idProduitEpargne;

	@Column(name = "code_produit", nullable = false, length = 50, unique = true)
	// Code unique du produit (ex: EP-VUE).
	private String codeProduit;

	@Column(nullable = false, length = 150)
	// Libelle du produit d'epargne.
	private String libelle;

	@Column(name = "taux_interet_annuel", nullable = false, precision = 7, scale = 4)
	// Taux d'interet annuel applique a l'epargne.
	private BigDecimal tauxInteretAnnuel;

	@Column(name = "montant_min_ouverture", precision = 19, scale = 2)
	// Montant minimum requis pour ouvrir ce type d'epargne.
	private BigDecimal montantMinOuverture;

	@Column(name = "penalite_retrait_anticipe", precision = 7, scale = 4)
	// Pourcentage de penalite en cas de retrait avant le terme.
	private BigDecimal penaliteRetraitAnticipe;

	@Column(name = "duree_min_jours")
	// Duree minimale de placement en jours (pour les DAT).
	private Integer dureeMinJours;

	@Column(name = "est_actif", nullable = false)
	// Indique si le produit est actuellement commercialise.
	private Boolean estActif = true;
}
