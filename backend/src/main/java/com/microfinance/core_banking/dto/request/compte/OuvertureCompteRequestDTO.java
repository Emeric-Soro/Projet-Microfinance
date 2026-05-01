package com.microfinance.core_banking.dto.request.compte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OuvertureCompteRequestDTO {

    @NotNull(message = "L'id client est obligatoire")
    private Long idClient;

    @NotBlank(message = "Le code type compte est obligatoire")
    @Size(max = 50, message = "Le code type compte ne doit pas depasser 50 caracteres")
    private String codeTypeCompte;

    @NotNull(message = "Le depot initial est obligatoire")
    @Positive(message = "Le depot initial doit etre strictement positif")
    private BigDecimal depotInitial;
}
