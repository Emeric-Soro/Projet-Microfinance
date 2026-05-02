package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TesterSchemaComptableRequestDTO {
    @NotBlank(message = "Le code operation est obligatoire")
    @Size(max = 50)
    private String codeOperation;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal montant;

    private BigDecimal frais;

    @Size(max = 100)
    private String referenceDebit;

    @Size(max = 255)
    private String libelleDebit;

    @Size(max = 100)
    private String referenceCredit;

    @Size(max = 255)
    private String libelleCredit;

    @Size(max = 100)
    private String referenceFrais;

    @Size(max = 255)
    private String libelleFrais;
}
