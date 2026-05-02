package com.microfinance.core_banking.dto.request.extension;

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
public class CreerEcritureManuelleRequestDTO {
    @Size(max = 50)
    private String referencePiece;

    @Size(max = 10)
    private String codeJournal;

    private LocalDate dateComptable;

    private LocalDate dateValeur;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 255)
    private String libelle;

    @Size(max = 50)
    private String referenceSource;

    @NotEmpty(message = "Au moins une ligne d'ecriture est requise")
    @Valid
    private List<LigneEcritureDTO> lignes;
}
