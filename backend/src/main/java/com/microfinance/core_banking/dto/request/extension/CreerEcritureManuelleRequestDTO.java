package com.microfinance.core_banking.dto.request.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de création d'une écriture comptable manuelle")
public class CreerEcritureManuelleRequestDTO {
    @Size(max = 50)
    @Schema(description = "Référence de la pièce justificative (optionnel, max 50 caractères)", example = "PIECE-001")
    private String referencePiece;

    @Size(max = 10)
    @Schema(description = "Code du journal (optionnel, max 10 caractères)", example = "JRN-01")
    private String codeJournal;

    @Schema(description = "Date comptable (optionnel)", example = "2026-04-01")
    private LocalDate dateComptable;

    @Schema(description = "Date de valeur (optionnel)", example = "2026-04-01")
    private LocalDate dateValeur;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 255)
    @Schema(description = "Libellé de l'écriture (obligatoire, max 255 caractères)", example = "Écriture manuelle de régularisation")
    private String libelle;

    @Size(max = 50)
    @Schema(description = "Référence source (optionnel, max 50 caractères)", example = "SRC-001")
    private String referenceSource;

    @NotEmpty(message = "Au moins une ligne d'ecriture est requise")
    @Valid
    @Schema(description = "Liste des lignes d'écriture")
    private List<LigneEcritureDTO> lignes;
}
