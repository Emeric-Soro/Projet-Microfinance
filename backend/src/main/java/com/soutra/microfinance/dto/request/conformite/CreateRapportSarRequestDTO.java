package com.soutra.microfinance.dto.request.conformite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRapportSarRequestDTO {

    private Long idClient;

    @NotBlank(message = "Le type d'alerte est obligatoire")
    private String typeAlerte;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Positive(message = "Le montant soupconne doit etre positif")
    private BigDecimal montantSoupconne;

    @NotBlank(message = "Le soumisPar est obligatoire")
    private String soumisPar;
}
