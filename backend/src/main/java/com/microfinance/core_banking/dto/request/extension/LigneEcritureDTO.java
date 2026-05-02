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
public class LigneEcritureDTO {
    @NotBlank(message = "Le numero de compte est obligatoire")
    @Size(max = 20)
    private String numeroCompte;

    @NotBlank(message = "Le sens (DEBIT/CREDIT) est obligatoire")
    private String sens;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal montant;

    @Size(max = 100)
    private String referenceAuxiliaire;

    @Size(max = 255)
    private String libelleAuxiliaire;
}
