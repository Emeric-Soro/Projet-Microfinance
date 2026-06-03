package com.soutra.microfinance.dto.response.credit;

public record DecisionCreditResponseDTO(
        String decision,
        CreditResponseDTO credit,
        DemandeCreditResponseDTO demande
) {}
