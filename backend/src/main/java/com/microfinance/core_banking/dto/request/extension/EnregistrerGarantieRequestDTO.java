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
public class EnregistrerGarantieRequestDTO {
    @NotBlank(message = "Le type de garantie est obligatoire")
    @Size(max = 50)
    private String typeGarantie;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 500)
    private String description;

    @NotNull(message = "La valeur est obligatoire")
    @Positive
    private BigDecimal valeur;

    private BigDecimal valeurNantie;

    @Size(max = 20)
    private String statut;
}
