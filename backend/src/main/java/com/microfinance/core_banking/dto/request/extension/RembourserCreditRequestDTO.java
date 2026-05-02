package com.microfinance.core_banking.dto.request.extension;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RembourserCreditRequestDTO {
    @NotNull(message = "Le montant est obligatoire")
    @Positive
    private BigDecimal montant;

    @NotBlank(message = "Le numero de compte source est obligatoire")
    @Size(max = 30)
    private String numCompteSource;

    private Long idUtilisateurOperateur;

    @Size(max = 50)
    private String referenceTransaction;

    @Size(max = 50)
    private String referenceRemboursement;

    private LocalDate datePaiement;
}
