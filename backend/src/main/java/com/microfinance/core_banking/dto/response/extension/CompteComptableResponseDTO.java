package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Compte comptable du plan comptable")
public class CompteComptableResponseDTO {
    @Schema(description = "Identifiant unique du compte comptable", example = "1")
    private Long idCompteComptable;

    @Schema(description = "Numéro du compte comptable", example = "512000")
    private String numeroCompte;

    @Schema(description = "Intitulé du compte", example = "Banque")
    private String intitule;

    @Schema(description = "Type de solde (DEBITEUR/CREDITEUR)", example = "DEBITEUR")
    private String typeSolde;

    @Schema(description = "Indique si le compte est un compte interne", example = "false")
    private Boolean compteInterne;

    @Schema(description = "Classe comptable associée", example = "Classe 5")
    private String classe;

    @Schema(description = "Agence de rattachement", example = "Agence Dakar Plateau")
    private String agence;
}
