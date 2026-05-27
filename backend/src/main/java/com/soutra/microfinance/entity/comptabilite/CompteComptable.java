package com.soutra.microfinance.entity.comptabilite;

import com.soutra.microfinance.entity.BaseAuditEntity;
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
@Table(name = "compte_comptable")
// Plan comptable SYSCOHADA-SFD : represente un compte du grand livre general.
public class CompteComptable extends BaseAuditEntity {

    @Id
    @Column(name = "numero_compte", length = 20)
    // Numero normalise SYSCOHADA du compte (ex: 15110000).
    private String numeroCompte;

    @Column(nullable = false, length = 150)
    // Intitule officiel du compte.
    private String intitule;

    @Column(nullable = false, length = 20)
    // Classe SYSCOHADA (CLASSE_1 a CLASSE_7).
    private String classe;

    @Column(name = "nature_solde", nullable = false, length = 10)
    // Nature du solde normal : DEBITEUR ou CREDITEUR.
    private String natureSolde;

    @Column(nullable = false, precision = 19, scale = 2)
    // Solde courant du compte dans le grand livre.
    private BigDecimal solde;

    @Column(name = "actif_sn", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    // Indique si le compte est actif (Y) ou ferme (N).
    private String actifSn;
}
