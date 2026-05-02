package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class JournalComptableResponseDTO {
    private Long idJournalComptable;
    private String codeJournal;
    private String libelle;
    private String typeJournal;
    private Boolean actif;
}
