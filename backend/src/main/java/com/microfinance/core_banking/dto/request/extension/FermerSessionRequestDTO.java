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
public class FermerSessionRequestDTO {
    @NotNull(message = "Le solde final est obligatoire")
    @Positive
    private BigDecimal soldeFinal;

    @Size(max = 500)
    private String observation;

    private String dateFermeture;
}
