package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'un journal comptable")
public class CreerJournalComptableRequestDTO {
    @NotBlank(message = "Le code journal est obligatoire")
    @Size(max = 10)
    @Schema(description = "Code du journal (obligatoire, max 10 caractères)", example = "JRN-01")
    private String codeJournal;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    @Schema(description = "Libellé du journal (obligatoire, max 100 caractères)", example = "Journal des opérations diverses")
    private String libelle;

    @NotBlank(message = "Le type journal est obligatoire")
    @Size(max = 20)
    @Schema(description = "Type de journal (obligatoire, max 20 caractères)", example = "OPERATIONS_DIVERSES")
    private String typeJournal;

    @Schema(description = "Journal actif (optionnel)", example = "true")
    private Boolean actif;
}
