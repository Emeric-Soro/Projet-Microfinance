package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DelesterCaisseRequestDTO {
    @NotNull(message = "L'id session caisse est obligatoire")
    private Long idSessionCaisse;

    @NotNull(message = "Le montant est obligatoire")
    @Positive
    private BigDecimal montant;

    @NotNull(message = "L'id guichetier est obligatoire")
    private Long idGuichetier;

    @Size(max = 500)
    private String motif;
}
