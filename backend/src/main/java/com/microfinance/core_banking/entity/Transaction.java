package com.microfinance.core_banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_transaction")
public class Transaction extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    @Column(name = "reference_unique", nullable = false, length = 80, unique = true)
    private String referenceUnique;

    @Column(name = "date_heure_transaction", nullable = false)
    private LocalDateTime dateHeureTransaction;

    @Column(name = "montant_global", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantGlobal;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal frais = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_operation", nullable = false, length = 30)
    private StatutOperation statutOperation = StatutOperation.EN_ATTENTE;

    @Column(name = "validation_superviseur_requise", nullable = false)
    private Boolean validationSuperviseurRequise = Boolean.FALSE;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "date_execution")
    private LocalDateTime dateExecution;

    @Column(name = "motif_rejet", length = 500)
    private String motifRejet;

    @Column(name = "code_operation_metier", length = 40)
    private String codeOperationMetier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_user_validation")
    private Utilisateur utilisateurValidation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_transaction", nullable = false)
    private TypeTransaction typeTransaction;

    @ManyToOne
    @JoinColumn(name = "id_compte_source")
    private Compte compteSource;

    @ManyToOne
    @JoinColumn(name = "id_compte_destination")
    private Compte compteDestination;

    @ManyToOne
    @JoinColumn(name = "id_session_caisse")
    private SessionCaisse sessionCaisse;

    @ManyToOne
    @JoinColumn(name = "id_agence_operation")
    private Agence agenceOperation;

    @OneToMany(mappedBy = "transaction", targetEntity = LigneEcriture.class)
    private List<LigneEcriture> lignesEcriture = new ArrayList<>();
}
