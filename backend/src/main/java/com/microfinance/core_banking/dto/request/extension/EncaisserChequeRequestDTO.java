package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EncaisserChequeRequestDTO {
    @NotNull(message = "L'id de la remise de cheque est obligatoire")
    private Long idRemiseCheque;
}
