package com.microfinance.core_banking.dto.response.compte;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un compte bancaire")
public class CompteResponseDTO {

    @Schema(description = "Numéro de compte", example = "SN12345678901234567890")
    private String numCompte;

    @Schema(description = "Type de compte", example = "EPARGNE")
    private String typeCompte;

    @Schema(description = "Solde actuel du compte en XOF", example = "150000.00")
    private BigDecimal solde;

    @Schema(description = "Devise du compte", example = "XOF")
    private String devise;

    @Schema(description = "Montant du découvert autorisé en XOF", example = "50000.00")
    private BigDecimal decouvertAutorise;

    @Schema(description = "Statut du compte", example = "ACTIF")
    private String statut;
}
