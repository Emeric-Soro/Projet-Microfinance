package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de commission inter-agence")
public class CommissionInterAgenceRequestDTO {
    @Schema(description = "Taux de commission (optionnel)", example = "0.5")
    private BigDecimal tauxCommission;
    @Schema(description = "Montant de la commission (optionnel)", example = "2500.00")
    private BigDecimal montantCommission;
    @Schema(description = "Identifiant du compte comptable (optionnel)", example = "1")
    private Long idCompteComptable;
    @Schema(description = "Statut de la commission (optionnel)", example = "CALCULEE")
    private String statutCommission;
    @Schema(description = "Référence de la pièce justificative (optionnel)", example = "REF-20260401-001")
    private String referencePiece;
    @Schema(description = "Date de comptabilisation (optionnel)", example = "2026-04-01T14:30:00")
    private LocalDateTime dateComptabilisation;
}
