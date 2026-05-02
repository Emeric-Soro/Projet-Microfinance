package com.microfinance.core_banking.dto.request.operation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionTransactionRequestDTO {

    @Size(max = 500, message = "Le motif ne doit pas depasser 500 caracteres")
    private String motif;
}
