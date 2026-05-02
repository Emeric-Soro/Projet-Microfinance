package com.microfinance.core_banking.dto.response.extension;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LigneSchemaTestDTO {
    private String numeroCompte;
    private String sens;
    private BigDecimal montant;
    private String referenceAuxiliaire;
    private String libelleAuxiliaire;
}
