package com.microfinance.core_banking.dto.request.loan;

import com.microfinance.core_banking.entity.LoanFacility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanFacilityRequestDTO {

    @NotNull(message = "Le client est obligatoire")
    private Long customerId;

    private Long productId;

    @NotNull(message = "Le montant principal est obligatoire")
    @Positive(message = "Le montant principal doit etre positif")
    private BigDecimal principalAmount;

    @NotNull(message = "La duree du terme est obligatoire")
    @Positive(message = "Le terme doit etre positif")
    private Integer termMonths;

    private LocalDate startDate;
    private LocalDate endDate;
    // Optional: status as a string value representing LoanFacility.LoanStatus
    private String status;
}
