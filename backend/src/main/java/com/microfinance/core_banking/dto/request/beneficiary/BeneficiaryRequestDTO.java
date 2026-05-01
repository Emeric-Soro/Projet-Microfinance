package com.microfinance.core_banking.dto.request.beneficiary;
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
public class BeneficiaryRequestDTO {

    @NotNull(message = "Le loan_facility_id est obligatoire")
    private Long loanFacilityId;

    @NotNull(message = "Le compte bénéficiaire est obligatoire")
    private String beneficiaryAccount;

    @NotNull(message = "Le nom du bénéficiaire est obligatoire")
    private String beneficiaryName;

    private BigDecimal share;
}
