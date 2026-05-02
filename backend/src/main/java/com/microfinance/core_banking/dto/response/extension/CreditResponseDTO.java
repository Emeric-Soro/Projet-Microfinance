package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Crédit accordé à un client")
public class CreditResponseDTO {
    @Schema(description = "Identifiant unique du crédit", example = "1")
    private Long idCredit;

    @Schema(description = "Référence du crédit", example = "CRED-20260401-0001")
    private String referenceCredit;

    @Schema(description = "Identifiant du client emprunteur", example = "1")
    private Long idClient;

    @Schema(description = "Montant accordé en XOF", example = "1500000.00")
    private BigDecimal montantAccorde;

    @Schema(description = "Taux d'intérêt annuel en pourcentage", example = "8.50")
    private BigDecimal tauxAnnuel;

    @Schema(description = "Montant de la mensualité en XOF", example = "75000.00")
    private BigDecimal mensualite;

    @Schema(description = "Capital restant dû en XOF", example = "1200000.00")
    private BigDecimal capitalRestantDu;

    @Schema(description = "Frais prélevés au déblocage en XOF", example = "15000.00")
    private BigDecimal fraisPreleves;

    @Schema(description = "Référence de la transaction de déblocage", example = "TXN-20260401-000001")
    private String referenceTransactionDeblocage;

    @Schema(description = "Statut du crédit", example = "ACTIF")
    private String statut;
}
