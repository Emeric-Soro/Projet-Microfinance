package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Détail du billetage d'une caisse")
public class BilletageCaisseResponseDTO {
    @Schema(description = "Identifiant unique du billetage", example = "1")
    private Long idBilletage;

    @Schema(description = "Identifiant de la session caisse", example = "1")
    private Long idSessionCaisse;

    @Schema(description = "Valeur de la coupure en XOF", example = "10000.00")
    private BigDecimal coupure;

    @Schema(description = "Quantité de billets/pièces", example = "50")
    private Integer quantite;

    @Schema(description = "Total de cette coupure (coupure * quantité)", example = "500000.00")
    private BigDecimal total;

    @Schema(description = "Type de billetage: BILLET ou PIECE", example = "BILLET")
    private String typeBilletage;

    @Schema(description = "Date et heure du billetage", example = "2026-05-02T17:00:00")
    private LocalDateTime dateBilletage;
}
