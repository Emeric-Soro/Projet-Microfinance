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
public class CreerOrdreCompensationRequestDTO {
    @NotBlank(message = "Le code ordre est obligatoire")
    @Size(max = 20)
    private String codeOrdre;

    @NotNull(message = "L'id lot est obligatoire")
    private Long idLot;

    @NotNull(message = "L'id transaction est obligatoire")
    private Long idTransaction;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    private BigDecimal montant;

    @NotBlank(message = "Le sens est obligatoire")
    @Size(max = 10)
    private String sens;

    @Size(max = 20)
    private String statut;
}
