package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommanderChequierRequestDTO {
    @NotNull(message = "L'id du compte est obligatoire")
    private Long idCompte;

    @NotNull
    @Positive
    private Integer nombreCheques;

    @NotBlank(message = "Le premier numero est obligatoire")
    private String premierNumero;
}
