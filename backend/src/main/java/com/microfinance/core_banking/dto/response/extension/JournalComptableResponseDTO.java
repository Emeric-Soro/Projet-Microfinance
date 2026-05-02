package com.microfinance.core_banking.dto.response.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Journal comptable")
public class JournalComptableResponseDTO {
    @Schema(description = "Identifiant unique du journal comptable", example = "1")
    private Long idJournalComptable;

    @Schema(description = "Code du journal", example = "CAIS")
    private String codeJournal;

    @Schema(description = "Libellé du journal", example = "Caisse")
    private String libelle;

    @Schema(description = "Type de journal", example = "TRESORERIE")
    private String typeJournal;

    @Schema(description = "Indique si le journal est actif", example = "true")
    private Boolean actif;
}
