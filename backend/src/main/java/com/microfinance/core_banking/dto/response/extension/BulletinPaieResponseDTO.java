package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Bulletin de paie d'un employé")
public class BulletinPaieResponseDTO {
    @Schema(description = "Identifiant unique du bulletin de paie", example = "1")
    private Long idBulletinPaie;

    @Schema(description = "Nom de l'employé", example = "M. Fall")
    private String employe;

    @Schema(description = "Période de paie", example = "2026-04")
    private String periode;

    @Schema(description = "Salaire brut en XOF", example = "500000.00")
    private BigDecimal salaireBrut;

    @Schema(description = "Montant des retenues en XOF", example = "100000.00")
    private BigDecimal retenues;

    @Schema(description = "Salaire net en XOF", example = "400000.00")
    private BigDecimal salaireNet;

    @Schema(description = "Statut du bulletin", example = "VALIDE")
    private String statut;
}
