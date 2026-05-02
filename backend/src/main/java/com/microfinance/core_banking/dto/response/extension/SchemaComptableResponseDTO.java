package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;

@Data
public class SchemaComptableResponseDTO {
    private Long idSchemaComptable;
    private String codeOperation;
    private String compteDebit;
    private String compteCredit;
    private String compteFrais;
    private String journalCode;
    private Boolean actif;
}
