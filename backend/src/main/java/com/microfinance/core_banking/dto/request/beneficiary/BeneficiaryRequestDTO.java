package com.microfinance.core_banking.dto.request.beneficiary;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de création d'un bénéficiaire de prêt")
public class BeneficiaryRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    @Schema(description = "Identifiant du prêt (obligatoire)", example = "1")
    private Long loanFacilityId;

    @NotNull(message = "Le compte bénéficiaire est obligatoire")
    @Schema(description = "Compte du bénéficiaire (obligatoire)", example = "SN000012345678901")
    private String beneficiaryAccount;

    @NotNull(message = "Le nom du bénéficiaire est obligatoire")
    @Schema(description = "Nom du bénéficiaire (obligatoire)", example = "Mamadou Diop")
    private String beneficiaryName;

    @Schema(description = "Part en pourcentage (optionnel)", example = "50.00")
    private BigDecimal share;
}
