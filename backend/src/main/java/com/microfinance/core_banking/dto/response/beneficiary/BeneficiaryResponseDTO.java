package com.microfinance.core_banking.dto.response.beneficiary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Informations d'un bénéficiaire associé à une facilité de prêt")
public class BeneficiaryResponseDTO {
    @Schema(description = "Identifiant unique du bénéficiaire", example = "1")
    private Long id;

    @Schema(description = "Identifiant de la facilité de prêt associée", example = "1")
    private Long loanFacilityId;

    @Schema(description = "Numéro de compte du bénéficiaire", example = "SN1234567890")
    private String beneficiaryAccount;

    @Schema(description = "Nom complet du bénéficiaire", example = "Jean Dupont")
    private String beneficiaryName;

    @Schema(description = "Part du bénéficiaire en pourcentage ou montant", example = "50.00")
    private BigDecimal share;
}
