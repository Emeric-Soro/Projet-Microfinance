package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SchemaTestResponseDTO {
    private String codeOperation;
    private String journalCode;
    private BigDecimal montantOperation;
    private BigDecimal frais;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private Boolean equilibree;
    private List<LigneSchemaTestDTO> lignes;
}
