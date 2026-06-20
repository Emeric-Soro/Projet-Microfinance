package com.soutra.microfinance.dto.request.mobile;

import com.soutra.microfinance.entity.MethodeCalculInteret;
import jakarta.validation.constraints.NotNull;
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
public class MobileSimulationCreditRequestDTO {

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit etre positif")
    private BigDecimal montant;

    @NotNull(message = "La duree est obligatoire")
    @Positive(message = "La duree doit etre positive")
    private Integer dureeMois;

    @NotNull(message = "Le taux annuel est obligatoire")
    @Positive(message = "Le taux annuel doit etre positif")
    private BigDecimal tauxAnnuel;

    private String methodeCalcul = MethodeCalculInteret.DEGRESSIF.name();

    private String codeProduit;

    public MethodeCalculInteret getMethodeCalculInteret() {
        return MethodeCalculInteret.valueOf(methodeCalcul.trim().toUpperCase());
    }
}
