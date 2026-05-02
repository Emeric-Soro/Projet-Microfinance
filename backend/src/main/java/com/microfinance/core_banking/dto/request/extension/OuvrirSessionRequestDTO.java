package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OuvrirSessionRequestDTO {
    @NotNull(message = "L'id caisse est obligatoire")
    private Long idCaisse;

    @NotNull(message = "L'id guichetier est obligatoire")
    private Long idGuichetier;

    @NotNull(message = "Le solde initial est obligatoire")
    @Positive
    private BigDecimal soldeInitial;

    private String dateOuverture;
}
