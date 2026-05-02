package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreerJournalComptableRequestDTO {
    @NotBlank(message = "Le code journal est obligatoire")
    @Size(max = 10)
    private String codeJournal;

    @NotBlank(message = "Le libelle est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "Le type journal est obligatoire")
    @Size(max = 20)
    private String typeJournal;

    private Boolean actif;
}
