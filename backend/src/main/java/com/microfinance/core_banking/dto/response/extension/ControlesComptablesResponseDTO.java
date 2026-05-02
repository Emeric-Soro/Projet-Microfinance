package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Résultat des contrôles comptables sur une période")
public class ControlesComptablesResponseDTO {
    @Schema(description = "Date de début de la période contrôlée", example = "2026-04-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de la période contrôlée", example = "2026-04-30")
    private LocalDate dateFin;

    @Schema(description = "Nombre total d'écritures comptables", example = "500")
    private int totalEcritures;

    @Schema(description = "Nombre total de lignes d'écritures", example = "1000")
    private int totalLignes;

    @Schema(description = "Total des montants au débit en XOF", example = "50000000.00")
    private BigDecimal totalDebit;

    @Schema(description = "Total des montants au crédit en XOF", example = "50000000.00")
    private BigDecimal totalCredit;

    @Schema(description = "Indique si l'équilibre global est respecté", example = "true")
    private Boolean equilibreGlobal;

    @Schema(description = "Nombre d'écritures sans lignes détaillées", example = "0")
    private int ecrituresSansLignes;

    @Schema(description = "Liste des écritures déséquilibrées détectées")
    private List<EcritureDesequilibreeDTO> ecrituresDesequilibrees;
}
