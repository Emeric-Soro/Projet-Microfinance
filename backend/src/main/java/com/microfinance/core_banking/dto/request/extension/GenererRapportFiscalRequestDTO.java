package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenererRapportFiscalRequestDTO {
    @NotBlank(message = "L'exercice fiscal est obligatoire")
    @Size(max = 20)
    private String exerciceFiscal;

    @NotBlank(message = "La date d'arreté est obligatoire")
    private String dateArrete;

    @Size(max = 20)
    private String format;
}
