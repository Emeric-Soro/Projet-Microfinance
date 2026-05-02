package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GenererRapportPrudentielRequestDTO {
    @NotBlank(message = "La date d'arreté est obligatoire")
    private String dateArrete;

    @Size(max = 50)
    private String typePerimetre;

    @Size(max = 20)
    private String format;
}
