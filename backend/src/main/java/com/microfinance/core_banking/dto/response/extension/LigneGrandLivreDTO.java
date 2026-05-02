package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LigneGrandLivreDTO {
    private LocalDate dateComptable;
    private String referencePiece;
    private String libelle;
    private String sens;
    private BigDecimal montant;
    private BigDecimal solde;
    private String sourceType;
    private String sourceReference;
}
