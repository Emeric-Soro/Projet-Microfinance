package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DeciderDemandeCreditRequestDTO {
    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20)
    private String statut;

    @Size(max = 1000)
    private String avisComite;

    @Size(max = 20)
    private String decisionFinale;
}
