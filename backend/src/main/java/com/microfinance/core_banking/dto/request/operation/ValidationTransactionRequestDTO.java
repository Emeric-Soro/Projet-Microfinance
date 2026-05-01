package com.microfinance.core_banking.dto.request.operation;

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
public class ValidationTransactionRequestDTO {

    @NotNull(message = "L'id du superviseur est obligatoire")
    private Long idSuperviseur;

    @Size(max = 500, message = "Le motif ne doit pas depasser 500 caracteres")
    private String motif;
}
