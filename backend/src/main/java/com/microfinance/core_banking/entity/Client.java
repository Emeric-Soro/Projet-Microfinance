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
@Table(name = "client")
// Entite representant un client de la microfinance.
public class Client extends BaseAuditEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_client")
	// Identifiant unique du client.
	private Long idClient;

	@Column(name = "code_client", nullable = false, length = 50, unique = true)
	// Code unique du client.
	private String codeClient;

	@Column(nullable = false, length = 100)
	// Nom du client.
	private String nom;

	@Column(nullable = false, length = 100)
	// Prenom du client.
	private String prenom;

	@Column(name = "date_naissance")
	// Date de naissance du client.
	private LocalDate dateNaissance;

	@Column
	// Adresse principale du client.
	private String adresse;

	@Column(length = 30, unique = true)
	// Numero de telephone du client.
	private String telephone;

	@Column(length = 150, unique = true)
	// Adresse email du client.
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "type_piece_identite", length = 30)
	// Type de document d'identite fourni.
	private TypePieceIdentite typePieceIdentite;

	@Column(name = "numero_piece_identite", length = 80, unique = true)
	// Numero de piece d'identite du client.
	private String numeroPieceIdentite;

	@Column(name = "date_expiration_piece_identite")
	// Date d'expiration de la piece d'identite.
	private LocalDate dateExpirationPieceIdentite;

	@Column(name = "photo_identite_url", length = 255)
	// Reference de la photo ou du selfie de verification.
	private String photoIdentiteUrl;

	@Column(name = "justificatif_domicile_url", length = 255)
	// Reference du justificatif de domicile.
	private String justificatifDomicileUrl;

	@Column(name = "justificatif_revenus_url", length = 255)
	// Reference du justificatif de revenus ou d'activite.
	private String justificatifRevenusUrl;

	@Column(length = 120)
	// Profession declaree par le client.
	private String profession;

	@Column(length = 150)
	// Employeur ou structure d'activite.
	private String employeur;

	@Column(name = "pays_nationalite", length = 80)
	// Nationalite du client.
	private String paysNationalite;

	@Column(name = "pays_residence", length = 80)
	// Pays de residence principal.
	private String paysResidence;

	@Column(nullable = false)
	// Indique si le client est une personne politiquement exposee.
	private Boolean pep = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "niveau_risque", nullable = false, length = 20)
	// Niveau de risque conformite attribue.
	private NiveauRisqueClient niveauRisque = NiveauRisqueClient.FAIBLE;

	@Enumerated(EnumType.STRING)
	@Column(name = "statut_kyc", nullable = false, length = 30)
	// Etat du dossier KYC.
	private StatutKycClient statutKyc = StatutKycClient.BROUILLON;

	@Column(name = "date_soumission_kyc")
	// Date de soumission du dossier KYC.
	private LocalDate dateSoumissionKyc;

	@Column(name = "date_validation_kyc")
	// Date de validation ou de rejet du dossier KYC.
	private LocalDate dateValidationKyc;

	@Column(name = "commentaire_kyc", length = 500)
	// Commentaire du controle KYC.
	private String commentaireKyc;

	@Column(name = "validateur_kyc", length = 120)
	// Identite du validateur du dossier KYC.
	private String validateurKyc;

	@Column(name = "date_inscription", nullable = false)
	// Date d'inscription du client.
	private LocalDate dateInscription;

	// --- Champs KYC Microfinance ---

	// NOTE: champs KYC redondants supprimés (numero/type/profession déjà déclarés plus haut).
	// Les informations KYC complémentaires sont conservées via les champs existants
	// (typePieceIdentite, numeroPieceIdentite et profession déclarés plus haut).

	@Column(name = "revenu_mensuel", precision = 19, scale = 2)
	// Revenu mensuel estime du client en FCFA.
	private BigDecimal revenuMensuel;

	@Column(name = "secteur_activite", length = 100)
	// Secteur d'activite (commerce, agriculture, artisanat, etc.).
	private String secteurActivite;

	// --- Relations ---

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_statut_client", nullable = false)
	// Statut courant du client.
	private StatutClient statutClient;

	@OneToOne(mappedBy = "client")
	// Utilisateur applicatif lie a ce client.
	private Utilisateur utilisateur;

	@OneToMany(mappedBy = "client")
	// Notifications recues par ce client.
	private List<Notification> notifications = new ArrayList<>();

	@OneToMany(mappedBy = "client")
	// Comptes appartenant a ce client.
	private List<Compte> comptes = new ArrayList<>();

	@OneToMany(mappedBy = "client")
	// Demandes de credit soumises par ce client.
	private List<DemandeCredit> demandesCredit = new ArrayList<>();

	@OneToMany(mappedBy = "client")
	// Credits actifs de ce client.
	private List<Credit> credits = new ArrayList<>();
}
