package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Reporting synthétique du réseau d'agences")
public class ReseauReportingDTO {
    @Schema(description = "Identifiant de l'agence", example = "1")
    private Long idAgence;

    @Schema(description = "Code de l'agence", example = "AG-001")
    private String codeAgence;

    @Schema(description = "Nom de l'agence", example = "Agence Dakar Plateau")
    private String nomAgence;

    @Schema(description = "Nombre de clients", example = "1500")
    private Long clients;

    @Schema(description = "Nombre de comptes", example = "2000")
    private Long comptes;

    @Schema(description = "Nombre de crédits en cours", example = "500")
    private Long credits;

    @Schema(description = "Volume total des transactions en XOF", example = "500000000.00")
    private BigDecimal volumeTransactions;
}
