package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Lot de compensation des opérations interbancaires")
public class LotCompensationResponseDTO {
    @Schema(description = "Identifiant unique du lot de compensation", example = "1")
    private Long idLotCompensation;

    @Schema(description = "Référence du lot", example = "LOT-20260401-0001")
    private String referenceLot;

    @Schema(description = "Type de lot", example = "VIREMENT")
    private String typeLot;

    @Schema(description = "Date et heure de traitement", example = "2026-04-01T23:00:00")
    private LocalDateTime dateTraitement;

    @Schema(description = "Statut du lot", example = "ENVOYE")
    private String statut;

    @Schema(description = "Commentaire sur le lot", example = "Lot journalier des virements")
    private String commentaire;
}
