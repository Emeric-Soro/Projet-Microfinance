package com.microfinance.core_banking.dto.request.client;

import com.microfinance.core_banking.entity.NiveauRisqueClient;
import com.microfinance.core_banking.entity.StatutKycClient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecisionKycClientRequestDTO {

    @NotNull(message = "Le statut KYC cible est obligatoire")
    private StatutKycClient statutKyc;

    @NotNull(message = "Le niveau de risque est obligatoire")
    private NiveauRisqueClient niveauRisque;

    @Size(max = 500, message = "Le commentaire KYC ne doit pas depasser 500 caracteres")
    private String commentaire;

    @NotBlank(message = "Le validateur KYC est obligatoire")
    @Size(max = 120, message = "Le validateur KYC ne doit pas depasser 120 caracteres")
    private String validateurKyc;
}
